/**
 * 流程设计器ogflow全局配置
 */

var CONFIG;

// 全局配置变量
define(function() {

	// 流程设计器基础配置
	var OGFLOW_CONF = {
		// 节点文本字体大小
		nodeFontSize: "9pt",

		// 开始直角拐角
		isCurvel: false
	}

	// 人工活动节点配置
	var ACTIVITY = {

		width: 96,
		height: 50
	}

	// 路由线的配置
	var LINE = {

		// 边界箭头 输入起始水平坐标startx，结束endx
		startEndConfig: {
			// 启用
			flag: true,
			startx: 0,
			endx: document.body.clientWidth
		},

		// 边界箭头最小长度 单位px
		minArrows: 77,

		// 直角拐角长度  允许最大长度为10
		cornerL: 6,

		// 曲线拐弯默认长度
		curveL: 30
	}

	// 全局配置变量
	CONFIG = {
		OGFLOW_CONF: OGFLOW_CONF, // 常用配置
		ACTIVITY: ACTIVITY, // 活动节点
		LINE: LINE // 路由线
	}

	return CONFIG;
})