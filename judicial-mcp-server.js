import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import axios from 'axios';
import * as dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// 加载环境变量
dotenv.config({ path: join(__dirname, '..', '.env') });

// 司法专业知识库
const JUDICIAL_KNOWLEDGE_BASE = {
  // 司法行政纠纷类型和对应的复议机关
  administrativeDisputes: {
    "行政处罚": {
      authority: "上一级行政机关",
      timeLimit: "60日",
      description: "对行政处罚决定不服的，可以向上一级行政机关申请行政复议"
    },
    "行政许可": {
      authority: "行政许可机关的上一级主管部门",
      timeLimit: "60日",
      description: "对行政许可决定不服的，可以向上一级主管部门申请行政复议"
    },
    "行政强制": {
      authority: "上一级行政机关",
      timeLimit: "60日",
      description: "对行政强制措施不服的，可以向上一级行政机关申请行政复议"
    },
    "信息公开": {
      authority: "同级政府或上一级主管部门",
      timeLimit: "60日",
      description: "对政府信息公开决定不服的，可以申请行政复议"
    }
  },
  
  // 诉讼指引
  litigationGuide: {
    "一般行政诉讼": {
      court: "有管辖权的人民法院",
      timeLimit: "6个月",
      description: "公民、法人或其他组织认为行政机关的行政行为侵犯其合法权益的"
    },
    "行政复议": {
      authority: "上一级行政机关或同级政府",
      timeLimit: "60日",
      description: "公民、法人或其他组织认为行政机关的具体行政行为侵犯其合法权益的"
    },
    "国家赔偿": {
      authority: "赔偿义务机关所在地人民法院",
      timeLimit: "2年",
      description: "国家机关及其工作人员违法行使职权侵犯公民、法人和其他组织的合法权益造成损害的"
    }
  }
};

// 智能提问模板
const QUESTION_TEMPLATES = {
  basic: [
    "请详细描述您遇到的具体行政行为是什么？",
    "这个行政行为是哪个行政机关作出的？",
    "您认为这个行政行为哪些方面侵害了您的合法权益？",
    "您是否收到了相关的行政决定书或通知？",
    "请提供作出该行政行为的具体日期"
  ],
  evidence: [
    "请提供与本案相关的所有证据材料",
    "您是否有相关的合同、文件或证明材料？",
    "是否有证人能够证明相关事实？",
    "请提供之前的投诉、举报记录（如有）"
  ],
  administrative: [
    "您是否已经向上级行政机关申请过行政复议？",
    "您是否已经向其他部门反映过此问题？",
    "您对解决此问题有什么具体的诉求？",
    "您是否愿意通过调解方式解决争议？"
  ]
};

// 阿里云API配置（需要替换为实际的API密钥）
const ALI_CLOUD_CONFIG = {
  apiKey: process.env.ALI_CLOUD_API_KEY,
  endpoint: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
  model: 'qwen-turbo'
};

/**
 * 阿里云API调用函数
 */
async function callAliCloudAPI(messages, systemPrompt) {
  if (!ALI_CLOUD_CONFIG.apiKey) {
    throw new Error('阿里云API密钥未配置，请在环境变量中设置ALI_CLOUD_API_KEY');
  }

  try {
    const response = await axios.post(
      `${ALI_CLOUD_CONFIG.endpoint}/chat/completions`,
      {
        model: ALI_CLOUD_CONFIG.model,
        messages: [
          {
            role: 'system',
            content: systemPrompt || '你是一个专业的司法助手，专门处理司法行政纠纷复议案件。'
          },
          ...messages
        ],
        temperature: 0.3,
        max_tokens: 2000
      },
      {
        headers: {
          'Authorization': `Bearer ${ALI_CLOUD_CONFIG.apiKey}`,
          'Content-Type': 'application/json'
        }
      }
    );

    return response.data.choices[0].message.content;
  } catch (error) {
    console.error('阿里云API调用失败:', error.message);
    throw new Error('AI服务调用失败，请检查API配置');
  }
}

/**
 * 提取关键信息
 */
function extractKeyInfo(caseDescription) {
  const keyInfo = {
    disputeType: '',
    authority: '',
    timeLimit: '',
    description: '',
    jurisdiction: '',
    evidence: []
  };

  // 简单的关键词匹配逻辑
  const keywords = {
    '行政处罚': ['罚款', '拘留', '警告', '没收', '吊销', '责令'],
    '行政许可': ['许可证', '执照', '审批', '许可', '批准'],
    '行政强制': ['强制', '扣押', '查封', '冻结', '强制措施'],
    '信息公开': ['信息公开', '政府信息', '公开申请', '信息']
  };

  for (const [disputeType, terms] of Object.entries(keywords)) {
    if (terms.some(term => caseDescription.includes(term))) {
      keyInfo.disputeType = disputeType;
      const disputeInfo = JUDICIAL_KNOWLEDGE_BASE.administrativeDisputes[disputeType];
      keyInfo.authority = disputeInfo.authority;
      keyInfo.timeLimit = disputeInfo.timeLimit;
      keyInfo.description = disputeInfo.description;
      break;
    }
  }

  return keyInfo;
}

/**
 * 生成针对性的问题
 */
function generateQuestions(disputeType, caseInfo) {
  let questions = [...QUESTION_TEMPLATES.basic];
  
  if (disputeType) {
    questions = questions.concat(QUESTION_TEMPLATES.evidence);
  }
  
  questions = questions.concat(QUESTION_TEMPLATES.administrative);
  
  return questions;
}

// 创建MCP服务器
const server = new Server(
  {
    name: 'judicial-administrative-mcp',
    version: '1.0.0'
  },
  {
    capabilities: {
      tools: {
        listChanged: true
      }
    }
  }
);

// 注册工具列表
const tools = [
  {
    name: 'get_litigation_guide',
    description: '获取司法行政纠纷的诉讼指引和复议指导',
    inputSchema: {
      type: 'object',
      properties: {
        disputeType: {
          type: 'string',
          description: '纠纷类型（行政处罚、行政许可、行政强制、信息公开等）'
        },
        action: {
          type: 'string',
          description: '操作类型（litigation/review/both）',
          enum: ['litigation', 'review', 'both']
        }
      },
      required: ['disputeType', 'action']
    }
  },
  {
    name: 'extract_case_info',
    description: '从当事人描述中提取关键信息',
    inputSchema: {
      type: 'object',
      properties: {
        caseDescription: {
          type: 'string',
          description: '当事人对案件的详细描述'
        }
      },
      required: ['caseDescription']
    }
  },
  {
    name: 'generate_questions',
    description: '根据案件类型生成针对性的专业问题',
    inputSchema: {
      type: 'object',
      properties: {
        disputeType: {
          type: 'string',
          description: '纠纷类型'
        },
        caseInfo: {
          type: 'string',
          description: '案件基本信息'
        }
      },
      required: ['disputeType']
    }
  },
  {
    name: 'call_ai_assistant',
    description: '调用阿里云AI进行专业法律分析',
    inputSchema: {
      type: 'object',
      properties: {
        query: {
          type: 'string',
          description: '需要AI分析的法律问题'
        },
        context: {
          type: 'string',
          description: '案件背景信息'
        }
      },
      required: ['query']
    }
  }
];

// 处理工具调用
server.setRequestHandler('tools/call', async (request) => {
  const { name, arguments: args } = request.params;

  try {
    switch (name) {
      case 'get_litigation_guide': {
        const { disputeType, action } = args;
        let result = {};
        
        if (action === 'litigation' || action === 'both') {
          result.litigationGuide = JUDICIAL_KNOWLEDGE_BASE.litigationGuide['一般行政诉讼'];
        }
        
        if (action === 'review' || action === 'both') {
          if (JUDICIAL_KNOWLEDGE_BASE.administrativeDisputes[disputeType]) {
            result.reviewGuide = JUDICIAL_KNOWLEDGE_BASE.administrativeDisputes[disputeType];
          }
        }
        
        return {
          content: [
            {
              type: 'text',
              text: JSON.stringify(result, null, 2)
            }
          ]
        };
      }
      
      case 'extract_case_info': {
        const { caseDescription } = args;
        const keyInfo = extractKeyInfo(caseDescription);
        
        return {
          content: [
            {
              type: 'text',
              text: JSON.stringify({
                extractedInfo: keyInfo,
                analysis: `根据您描述的案件信息，我们识别出这可能是一起${keyInfo.disputeType || '未识别的'}类型的行政纠纷。`
              }, null, 2)
            }
          ]
        };
      }
      
      case 'generate_questions': {
        const { disputeType, caseInfo } = args;
        const questions = generateQuestions(disputeType, caseInfo);
        
        return {
          content: [
            {
              type: 'text',
              text: JSON.stringify({
                questions,
                instructions: '请当事人依次回答以下问题，以便我们提供更准确的法律指导：'
              }, null, 2)
            }
          ]
        };
      }
      
      case 'call_ai_assistant': {
        const { query, context } = args;
        const systemPrompt = `你是一个专业的司法助手，专门处理司法行政纠纷复议案件。
        请基于中国法律法规，提供专业、权威的法律建议。
        你的回答应当准确、客观，避免提供具体的法律意见替代专业律师的咨询。`;
        
        const messages = [
          {
            role: 'user',
            content: context ? `背景信息：${context}\n\n问题：${query}` : query
          }
        ];
        
        const aiResponse = await callAliCloudAPI(messages, systemPrompt);
        
        return {
          content: [
            {
              type: 'text',
              text: aiResponse
            }
          ]
        };
      }
      
      default:
        throw new Error(`未知的工具: ${name}`);
    }
  } catch (error) {
    return {
      content: [
        {
          type: 'text',
          text: `错误: ${error.message}`
        }
      ],
      isError: true
    };
  }
});

// 启动服务器
async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error('司法MCP服务器已启动');
}

main().catch((error) => {
  console.error('服务器启动失败:', error);
  process.exit(1);
});