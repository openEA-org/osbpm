/**
 * 节点函数扩展
 */

define(["jquery"], function($) {

	(function() {

		//解决保存后H5自动转换为小写问题
		if(Element.prototype.getAttribute) {
			Element.prototype._getAttribute = Element.prototype.getAttribute;
			Element.prototype.getAttribute = function(a) {
				var flag = this._getAttribute(a.toLowerCase());
				if(flag != null) {
					getsetAttr = 0;
					return flag;
				} else {
					getsetAttr = 1;
					return this._getAttribute(a);
				}
			}
		}

		//解决保存后H5自动转换为小写问题
		if(Element.prototype.setAttribute) {
			Element.prototype._setAttribute = Element.prototype.setAttribute;
			Element.prototype.setAttribute = function(a, b) {
				this.getAttribute(a);
				if(getsetAttr == 0) {
					return this._setAttribute(a.toLowerCase(), b);
				} else {
					return this._setAttribute(a, b);
				}
			}
		}

		// 折线对象扩展  PolyLine
		//=========================================

		Element.prototype.getLinePointsArr = function() {

			if(this.tagName == "polyline") {
				// 将points转换成points数组 格式{{x1,y1}，{x2,y2}，{x3,y3}}
				var points = this.getAttribute("points");
				var pointsArr = points.trim().split(" ");

				var t = {
					x: "0",
					y: "0"
				};
				for(var i = 0; i < pointsArr.length; i++) {

					t.x = pointsArr[i].split(",")[0];
					t.y = pointsArr[i].split(",")[1];

					pointsArr[i] = t;

					t = {};
				}

				return pointsArr;
			}

			return null;
		}

		/** 
		 * 获取线第一个点坐标
		 */
		Element.prototype.getFirstPoint = function() {

			if(this.tagName == "polyline") {

				var points = this.getAttribute("points");
				var pointsArr = points.trim().split(" ");

				var t = {
					id: this.id,
					x: pointsArr[0].split(",")[0],
					y: pointsArr[0].split(",")[1]
				};

				return t;
			}

			return null;
		}

		/** 
		 * 设置线的第一个点
		 */
		Element.prototype.setFirstPoint = function(firstPoint) {

			if(this.tagName == "polyline") {

				var points = "";
				var pointArr = this.getLinePointsArr();
				pointArr[0] = firstPoint;
				for(var i = 0; i < pointArr.length; i++) {
					points = points + " " + pointArr[i].x + "," + pointArr[i].y
				}

				points = points.trim();

				this.setAttribute("oldpoints", points);
				this.setAttribute("orpoints", points);
				this.setAttribute("points", points);
				AddRouterText(this, "MoveObj")
			}

			return this;
		}

		/**
		 * 获取线最后一个点坐标
		 */
		Element.prototype.getLastPoint = function() {

			if(this.tagName == "polyline") {

				var points = this.getAttribute("points");
				var pointsArr = points.trim().split(" ");

				var t = {
					id: this.id,
					x: pointsArr[pointsArr.length - 1].split(",")[0],
					y: pointsArr[pointsArr.length - 1].split(",")[1]
				};

				return t;
			}

			return this;
		}

		/** 
		 * 设置线的最后一个点
		 */
		Element.prototype.setLastPoint = function(endtPoint) {

			if(this.tagName == "polyline") {

				var points = "";
				var pointArr = this.getLinePointsArr();
				pointArr[pointArr.length - 1] = endtPoint;
				
				for(var i = 0; i < pointArr.length; i++) {
					points = points + " " + pointArr[i].x + "," + pointArr[i].y
				}

				points = points.trim();

				this.setAttribute("oldpoints", points);
				this.setAttribute("orpoints", points);
				this.setAttribute("points", points);
				AddRouterText(this, "MoveObj")
			}

			return this;
		}

		/**
		 * 设置线的points
		 * @param {Object} pointsJson
		 */
		Element.prototype.setPoints = function(pointsJson) {
			var points = "";

			if(this.tagName == "polyline") {
				for(var i = 0; i < pointsJson.length; i++) {
					points = points + " " + pointsJson[i].x + "," + pointsJson[i].y
				}

				points = points.trim();

				//				console.log(points);
				this.setAttribute("oldpoints", points);
				this.setAttribute("orpoints", points);
				this.setAttribute("points", points);
				AddRouterText(this, "MoveObj")
			}
			
			return this;
		}

	})()

	return {};
});