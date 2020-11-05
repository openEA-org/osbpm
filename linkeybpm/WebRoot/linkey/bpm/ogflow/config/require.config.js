/**
 * ogflow流程设计器 全局公共JS配置文件
 */
require.config({
	//使用UI解析的基础目录
	//	baseUrl: '',
	paths: {
		"jquery": "./lib/jquery-2.1.4",
		"CONFIG": "../config/config",
		"prototypeExt": "../js/module/prototypeExt",
		"dcanvas": "../js/module/dcanvas",
		"Tools": "../js/utils/alibao_common",
		"ajaxhook": "./lib/ajaxhook.min"
	},

	shim: {
		//dcanvas 依赖于 jquery
		'dcanvas': {
			deps: ['jquery'],
			exports: 'dcanvas'
		}
	}
});

//全局ajax请求拦截
require(["jquery", "CONFIG", "prototypeExt", "dcanvas", "Tools", "ajaxhook"], function($, CONFIG, prototypeExt, dcanvas, Tools, ajaxhook) {
})