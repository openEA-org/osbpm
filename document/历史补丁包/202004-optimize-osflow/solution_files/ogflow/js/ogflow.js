/**
 * 原生版流程设计器
 */
require.config({
	paths: {
		//相关JS引入
		"commonJS": "../config/require.config",
		"lkwf": "module/lkwf",
		"route": "module/route",
		"hevent": "module/hevent"
	}

});
require(["commonJS"], function(commonJS) {
	require(["jquery", "lkwf", "hevent"], function($, lkwf, hevent) {

	})

})