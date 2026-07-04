// // 浏览器安装时
// chrome.runtime.onInstalled.addListener(() =>{
//     chrome.action.setBadgeText({
//         text: 'OFF'
//  })

//  console.log("gfedg")
// })


// //当用户点击插件图标时
// chrome.action.onClicked.addListener(async (tab) =>{
//     //获取当前插件的一个状态
//     const prevState = await chrome.action.getBadgeText({ tabId: tab.id })
//     const nextState = prevState==='ON'?'OFF':'ON'

//     // 更新我们插件的状态文字
//     await chrome.action.setBadgeText({
//         tabId: tab.id,
//         text: nextState
//     })  



//     //如果当前插件状态是ON的时候，我们在当前标签的页面中嵌入一个JS文件
//     if(nextState==='ON'){
//     await chrome.scripting.executeScript({
//         target: {
//             tabId: tab.id
//         },
        
//         files: ['content-script.js']
//     })
// }


// })







