/**
 * 绘图工具
 */
var dcanvas;

define([], function() {

	// 初始化svg对象
	(function() {

		if(!window.svg) {
			window.svg = {};
		}
		window.svg = {
			/**
			 * 创建SVG标签【带命名空间的标签】
			 * @param {Object} name 标签名称
			 */
			create: function(name) {
				this.node = document.createElementNS("http://www.w3.org/2000/svg", name);
				return this;
			},
			/**
			 * 将标签添加到SVG面板上面  
			 * @param {Object} parent SVG面板
			 */
			appendTo: function(parent) {
				if(typeof this.node !== "undefined" && parent.nodeType == 1) {
					parent.appendChild(this.node);
				}
				return this;
			},
			/**
			 * 添加带命名空间的标签属性
			 * @param {Object} bag  JSON配置数据
			 */
			attrNS: function(bag) {
				for(var i in bag) {
					if(bag.hasOwnProperty(i)) {
						this.node.setAttributeNS(null, i, bag[i])
					}
				}
				return this;
			},
			/**
			 * 添加一般标签属性
			 * @param {Object} bag JSON配置数据
			 */
			attr: function(bag) {
				for(var i in bag) {
					this.node.setAttribute(i, bag[i])
				}
				return this;
			},
			/**
			 * 添加文本text
			 * @param {Object} text
			 */
			html: function(text) {
				if(text.nodeType == 3) {
					this.node.appendChild(document.createTextNode(text));
				}
				return this;
			}
		}
	})()

	/**
	 * 统一绘图工具
	 * @param {Object} coordinate 坐标节点，汇聚和拆分时为数组，其他时候为{x:10,y:10}
	 * @param {Object} node 节点对象
	 * @param {Object} type 汇聚线Lconverge、拆分线Lspit、普通线polyline、方形rect、文本text、路径path、菱形polygon、
	 */
	function drawCanvas(coordinate, node, type) {

	}

	/**
	 * 创建SVG标签到面板中
	 * @param {Object} svgpanel   SVG面板  <svg id="svg" width="100%" height="100%" version="1.1" xmlns="http://www.w3.org/2000/svg">
	 * @param {String} labelType  创建的标签类型 rect
	 * @param {JSON} attrNSJSON   SVG属性配置JSON
	 * @param {JSON} attrJSON     自定义属性配置JSON
	 */
	function createSVG(svgpanel, labelType, attrNSJSON, attrJSON) {

		var nodeObj = svg.create(labelType).attrNS(attrNSJSON).attr(attrJSON).appendTo(svgpanel).node;

		return nodeObj;
	}

	dcanvas = {
		drawCanvas: drawCanvas, // 统一绘图工具
		createSVG: createSVG // 创建SVG图形标签
	}
	return dcanvas;
})