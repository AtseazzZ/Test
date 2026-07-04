// 定义常量选择器
const SELECTORS = {
  VIDEO_WRAP: '.bpx-player-video-wrap',
  VIDEO: 'video',
  // 字幕按钮样式
  SUBTITLE_BUTTON: '.bpx-player-ctrl-subtitle-result',
  SUBTITLE_TOGGLE: '.bpx-player-ctrl-btn.bpx-player-ctrl-subtitle',
  // 中文字幕未开启样式
  CHINESE_LANGUAGE_OPTION: '.bpx-player-ctrl-subtitle-language-item[data-lan="ai-zh"]',
  // 中文字幕已开启样式
  ACTIVE_CHINESE_LANGUAGE: 'bpx-player-ctrl-subtitle-language-item.bpx-state-active[data-lan="ai-zh"]',
  MAX_RETRIES : 5 ,
}

// 定义常量时间间隔
const TIMING = {
  // 视频加载完后 两秒才开始操作
  INITIAL_SUBTITLE_DELAY: 2000,
  // 重试间隔
  SUBTITLE_CHECK_INTERVAL: 500,
  // 延迟点击字幕时间
  LANGUAGE_CLICK_DELAY: 100,
}

/**
 * 主入口函数
 */
function initSubtitleAutoOpen() {
  console.log('插件已开启-' + getCurrentTime())

  const videoWrapElement = document.querySelector(SELECTORS.VIDEO_WRAP)
  if (!videoWrapElement) return

  const videoElement = videoWrapElement.querySelector(SELECTORS.VIDEO)
  if (!videoElement) return

  console.log('找到了视频元素')

  // 监听视频加载完成事件
  videoElement.addEventListener('loadeddata', () => {
    // 使用正确的函数引用方式，避免立即执行
    setTimeout(openZm, TIMING.INITIAL_SUBTITLE_DELAY)
  })
}

/**
 * 尝试开启字幕
 */
function openZm() {
  let retryCount = 0

  const intervalId = setInterval(() => {

   // 检查重试次数是否超过限制
    if (retryCount >= SELECTORS.MAX_RETRIES) {
      console.log('已达到最大重试次数，停止尝试')
      clearInterval(intervalId)
      return
    }
    retryCount++  

    const subtitleBtn = getSubtitleButton()
    if (!subtitleBtn) {
      console.log('未找到字幕按钮')
      return
    }

    const subtitleToggle = document.querySelector(SELECTORS.SUBTITLE_TOGGLE)
    if (!subtitleToggle) return

    console.log('已找到字幕按钮')
    clearInterval(intervalId)

    // 使用正确的函数引用方式，避免立即执行
    setTimeout(clickChineseLanguageOption, TIMING.LANGUAGE_CLICK_DELAY)
  }, TIMING.SUBTITLE_CHECK_INTERVAL)
}

/**
 * 获取字幕按钮元素
 */
function getSubtitleButton() {
  return document.querySelector(SELECTORS.SUBTITLE_BUTTON)
}

/**
 * 检查字幕是否已经打开
 */
function isSubtitleOn() {
  const subtitleBtn = getSubtitleButton()
  if (!subtitleBtn) {
    console.log('字幕按钮元素 not found.')
    return false
  }

  // 检查是否有激活的中文字幕选项
  const activeLanguageItem = document.querySelector(SELECTORS.ACTIVE_CHINESE_LANGUAGE)
  if (activeLanguageItem) {
    console.log('检测到字幕已打开')
    return true
  }

  const chineseOption = document.querySelector(SELECTORS.CHINESE_LANGUAGE_OPTION)
  if (chineseOption) {
    console.log('字幕未打开')
    return false
  }

  return false
}

/**
 * 点击中文字幕选项
 */
function clickChineseLanguageOption() {
  if (!isSubtitleOn()) {
    const chineseOption = document.querySelector(SELECTORS.CHINESE_LANGUAGE_OPTION)
    if (chineseOption) {
      chineseOption.click()
      console.log('已开启字幕')
    }
  }
}

/**
 * 获取当前时间戳和格式化时间
 */
function getCurrentTime() {
  const stamp = new Date().getTime() + 8 * 60 * 60 * 1000
  const beijingTime = new Date(stamp).toISOString().replace(/T/, ' ').replace(/\..+/, '').substring(0, 19)

  return beijingTime + '_' + stamp
}

// 初始化插件
initSubtitleAutoOpen()
