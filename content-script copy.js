console.log('插件已开启-' + getCurrentTime())

// 监听video 如果加载了新视频就尝试开启字幕
var videoWrapElement = document.querySelector('.bpx-player-video-wrap')
if (videoWrapElement) {
  // 在视频包装元素下查找 <video> 标签
  var videoElement = videoWrapElement.querySelector('video')
  if (videoElement) {
    console.log('找到了视频元素')

    // 监听视频加载完成事件
    videoElement.addEventListener('loadeddata', function () {
      // 尝试开启字幕
      setTimeout(openZm(), 3000)
    })
  }
}

function openZm() {
  // 每隔1s尝试获取 字幕元素
  const intervalId = setInterval(function () {
    // 开启字幕操作
    var subtitleBtn = document.querySelector('.bpx-player-ctrl-subtitle-result')

    if (!subtitleBtn) {
      console.log('未找到字幕按钮')
      return
    }

    // 修改字幕按钮的class
    // 获取同时具有bpx-player-ctrl-btn和bpx-player-ctrl-subtitle类的元素
    var subtitleBtn = document.querySelector('.bpx-player-ctrl-btn.bpx-player-ctrl-subtitle')
    // 检查元素是否存在
    if (!subtitleBtn) {
      return
    }

    console.log('已找到字幕按钮')
    clearInterval(intervalId)

    // 点击中文字幕按钮
    setTimeout(clickChineseLanguageOption(), 1000)
  }, 2000)
}

// 判断字幕是否已经打开了
function zmIsOn() {
  var subtitleBtn = document.querySelector('.bpx-player-ctrl-subtitle-result')
  if (!subtitleBtn) {
    console.log('字幕按钮元素 not found.')
    return false
  }

  // 中文字幕是否已被勾选
  var activeLanguageItem = document.querySelector('bpx-player-ctrl-subtitle-language-item.bpx-state-active[data-lan="ai-zh"]')
  if (activeLanguageItem) {
    console.log('检测到字幕已打开')
    return true
  }

  var chineseOption = document.querySelector('.bpx-player-ctrl-subtitle-language-item[data-lan="ai-zh"]')
  if (chineseOption) {
    console.log('字幕未打开')
    return false
  }
}


// 点击中文语言选项
function clickChineseLanguageOption() {
  if (!zmIsOn()) {
    var chineseOption = document.querySelector('.bpx-player-ctrl-subtitle-language-item[data-lan="ai-zh"]')
    chineseOption.click()
    console.log('已开启字幕')
  }
}

function getCurrentTime() {
  // 获取当前北京时间的时间戳（单位为毫秒）
  var stamp = new Date().getTime() + 8 * 60 * 60 * 1000

  // 格式化北京时间为"YYYY-MM-DD HH:mm:ss"
  var beijingTime = new Date(stamp).toISOString().replace(/T/, ' ').replace(/\..+/, '').substring(0, 19)

  return beijingTime + '_' + stamp
}


