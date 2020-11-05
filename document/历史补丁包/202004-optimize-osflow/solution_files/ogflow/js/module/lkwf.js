/**
 * 流程设计器引擎
 */
var lkwf;
var pointR; //连线时提示小圆圈半径
var changeLineStyle = ""; // 鼠标移动时 线放大和改变颜色

define(["dcanvas", "jquery", "route"], function (dcanvas, $, route) {

    (function () {
        init();

        var startpointhint = document.getElementById("StartNodePoint");
        pointR = startpointhint.getAttribute("r"); //获取提示原点的半径
    })()

    /**
     * 流程设计器初始化
     */
    function init() {

        svginit(); // 样式描述初始化
    }


    /**
     * 获取线的points
     * @param {String} action 操作类型，有Creat/Edit/NodeMove
     */
    function GetPointsStr(action) {

        var clientX = event.clientX; //相对客户端X轴坐标 de
        var clientY = event.clientY;
        offsetX = parseInt(document.body.scrollLeft); //滚动条高度，在鼠标按下的时候初始化
        offsetY = parseInt(document.body.scrollTop);
        clientX += offsetX;
        clientY += offsetY;

        var mousex = clientX; // 鼠标弹起是x,y坐标
        var mousey = clientY;

        var k = CONFIG.LINE.curveL;
        var l = CONFIG.LINE.curveL;

        if (event.srcElement.tagName != "BODY") {
            if ((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") && event.button == leftbtn && !isAssist(event.srcElement)) {
                if (event.srcElement.parentNode.id == "svg") {
                    goalEndObj = event.srcElement
                } else {
                    goalEndObj = event.srcElement.parentNode
                }
            }
        }

        goalEndObj = unionNode(goalEndObj); // 转换为节点对象

        if (action == "Edit") { // 文字编辑   f/g代表的是偏移量
            var f = event.clientX - moveobjoldleft;
            var g = event.clientY - moveobjoldtop
        } else if (action == "Creat") { //创建线

            if (StartPointType == "boundaryLine" && goalEndObj && goalEndObj.tagName == "polyline") {
                goalEndObj = null;
            }

            getObjXY(goalEndObj, event.clientX, event.clientY, "Creat");
            var f = hotx2 - hotx;
            var g = hoty2 - hoty
            clientX = hotx2;
            clientY = hoty2;

        } else {
            var f = clientX - hotx;
            var g = clientY - hoty;
        }
        var h = f / 2;
        var i = g / 2;

        var j = hotx + "," + hoty + " " + hotx + "," + hoty; //默认初始化线
        var lineText = ""; // 线上默认的文字

        // 判断线的类型
        switch (StartPointType) {

            case "boundaryLine": // 边界箭头

                var minArrows = CONFIG.LINE.minArrows;

                j = hotx + "," + hoty + " " + clientX + "," + clientY;

                if (action == "Creat") {

                    var startEndConfig = CONFIG.LINE.startEndConfig;

                    if (hotEndType == "Bottom" && (hoty > clientY) && Tools.isNotBlank(goalEndObj)) {
                        if ((hoty - clientY) < minArrows) {
                            hoty = clientY + minArrows;
                        }
                        j = mousex + "," + hoty + " " + mousex + "," + clientY;
                        lineText = "机制";

                    } else if (hotEndType == "Top" && (hoty < clientY) && Tools.isNotBlank(goalEndObj)) {
                        if ((clientY - hoty) < minArrows) {
                            hoty = clientY - minArrows;
                        }
                        j = mousex + "," + hoty + " " + mousex + "," + clientY;
                        lineText = "控制";
                    } else if (hotEndType == "Left" && (hotx < clientX) && Tools.isNotBlank(goalEndObj)) {
                        if ((clientX - hotx) < minArrows) {
                            hotx = clientX - minArrows;
                        }
                        j = hotx + "," + mousey + " " + clientX + "," + mousey;

                        if (startEndConfig.flag) {
                            j = startEndConfig.startx + "," + mousey + " " + clientX + "," + mousey;
                        }

                        lineText = "输入";
                    } else if (hotType == "Right" && mousex > hotx) {
                        clientX = mousex;
                        if ((mousex - hotx) < minArrows) {
                            //hotx = mousex - minArrows;
                            clientX = hotx + minArrows;
                        }
                        j = hotx + "," + hoty + " " + clientX + "," + hoty;

                        if (startEndConfig.flag) {
                            j = hotx + "," + hoty + " " + startEndConfig.endx + "," + hoty;
                        }
                        lineText = "输出";
                    } else {
                        j = "";
                    }
                }

                break;
            case "innerLine": // 内部箭头
                hotEndType = getMousePointType(goalEndObj); // 鼠标相对连接结束节点位置计算
                var hotJson = gethotTypeJson(goalStartObj, goalEndObj, "innerLine");
                var starthot1 = "";
                var endhot1 = "";
                var difsx = hotx; // 连接点切换时起始值
                var difsy = hoty;

                if (Tools.isNotBlank(hotJson)) {
                    starthot1 = hotJson.starthotStr;
                    endhot1 = hotJson.endhotStr;
                    // 对象长宽
                    var ah = parseInt(converterSVG(goalStartObj, "height"));
                    var aw = parseInt(converterSVG(goalStartObj, "width"));
                    var bh = parseInt(converterSVG(goalEndObj, "height"));
                    var bw = parseInt(converterSVG(goalEndObj, "width"));

                    // 对象 xy
                    // 节点起始x,y
                    var ax = parseInt(converterSVG(goalStartObj, "left"));
                    var ay = parseInt(converterSVG(goalStartObj, "top"));
                    var bx = parseInt(converterSVG(goalEndObj, "left"));
                    var by = parseInt(converterSVG(goalEndObj, "top"));

                    if ((hotType == "Bottom" || hotType == "Top") && (hotType == hotEndType) && Math.abs(by - ay) < bh) {

                        if (hotType == "Bottom") {
                            k = (g >= 0) ? (Math.abs(g) + k) : k
                        }
                        if (hotType == "Top") {
                            k = (g < 0) ? -(Math.abs(g) + k) : -k
                        }

                        j = hotx + "," + hoty + " " + hotx + "," + (hoty + k) + " " + clientX + "," + (hoty + k) + " " + clientX + "," + clientY;
                        break;
                    } else if ((hotType == "Right" || hotType == "Left") && (hotType == hotEndType) && Math.abs(bx - ax) < bw) {
                        if (hotType == "Right") {
                            l = (f > 0) ? (Math.abs(f) + l) : l
                        }
                        if (hotType == "Left") {
                            l = (f < 0) ? -(Math.abs(f) + l) : -l
                        }
                        j = hotx + "," + hoty + " " + (hotx + l) + "," + hoty + " " + (hotx + l) + "," + clientY + " " + clientX + "," + clientY;

                        break;
                    }
                    if ((hotType == "Bottom" || hotType == "Top") && (hotType == hotEndType) && Math.abs(by - ay) < bh) {

                        if (hotType == "Bottom") {
                            k = (g >= 0) ? (Math.abs(g) + k) : k
                        }
                        if (hotType == "Top") {
                            k = (g < 0) ? -(Math.abs(g) + k) : -k
                        }

                        j = hotx + "," + hoty + " " + hotx + "," + (hoty + k) + " " + clientX + "," + (hoty + k) + " " + clientX + "," + clientY;
                        break;
                    } else if ((hotType == "Right" || hotType == "Left") && (hotType == hotEndType) && Math.abs(bx - ax) < bw) {
                        if (hotType == "Right") {
                            l = (f > 0) ? (Math.abs(f) + l) : l
                        }
                        if (hotType == "Left") {
                            l = (f < 0) ? -(Math.abs(f) + l) : -l
                        }
                        j = hotx + "," + hoty + " " + (hotx + l) + "," + hoty + " " + (hotx + l) + "," + clientY + " " + clientX + "," + clientY;

                        break;
                    } else {
                        // 开始连接点
                        if (Tools.isNotBlank(hotType) && starthot1.indexOf(hotType) == -1) {
                            if (hotType == "Bottom") {
                                difsy = parseInt(hoty - ah);
                            } else if (hotType == "Top") {
                                difsy = parseInt(hoty + ah);
                            } else if (hotType == "Left") {
                                difsx = parseInt(hotx + aw);
                            } else {
                                difsx = parseInt(hotx - aw);
                            }
                        }
                        // 结束连接点
                        if (Tools.isNotBlank(hotEndType) && endhot1.indexOf(hotEndType) == -1 && action == "Creat") {

                            if (hotEndType == "Bottom") {
                                clientY = parseInt(clientY - bh);
                            } else if (hotEndType == "Top") {
                                clientY = parseInt(clientY + bh);
                            } else if (hotEndType == "Left") {
                                clientX = parseInt(clientX + bw);
                            } else {
                                clientX = parseInt(clientX - bw);
                            }

                            h = (clientX - difsx) / 2;
                            i = (clientY - difsy) / 2;
                        }
                    }
                }

                if (hotType == "Bottom" || hotType == "Top") { //开始为上线
                    j = difsx + "," + difsy + " " + difsx + "," + (difsy + i) + " " + clientX + "," + (difsy + i) + " " + clientX + "," + clientY;

                    //					if(clientX > ax && clientX < parseInt(ax + aw)) { //中间为直线
                    //						j = clientX + "," + difsy + " " + clientX + "," + clientY;
                    //					}

                    if (hotEndType == "Right" || hotEndType == "Left") { //结束为左右
                        j = difsx + "," + difsy + " " + difsx + "," + clientY + " " + clientX + "," + clientY;
                    }

                } else {
                    j = difsx + "," + difsy + " " + (difsx + h) + "," + difsy + " " + (difsx + h) + "," + clientY + " " + clientX + "," + clientY;

                    //					if(clientY > ay && clientY < parseInt(ay + ah)) { //中间为直线
                    //						j = difsx + "," + clientY + " " + clientX + "," + clientY;
                    //					}
                    if (hotEndType == "Bottom" || hotEndType == "Top") { //结束为上下
                        j = difsx + "," + difsy + " " + clientX + "," + difsy + " " + clientX + "," + clientY;
                    }
                }

                break;
            case "freeLine":

                var pmove = { // 移动的点
                    _x: hotx,
                    _y: hoty
                }

                var pend = { // 结束点
                    _x: mousex,
                    _y: mousey
                }

                var beforepoint = ""; // 之前的点

                if (freeLineTemp != null) {

                    var pointsArr = freeLineTemp.points.split(" ");
                    var pe2 = {
                        _x: pointsArr[pointsArr.length - 1].split(",")[0],
                        _y: pointsArr[pointsArr.length - 1].split(",")[1]
                    }

                    pmove = {
                        _x: pe2._x,
                        _y: pe2._y
                    }

                    if (Tools.verdictXY(pe2._x, pe2._y, mousex, mousey) < 1) {
                        pend._y = pe2._y;
                    } else {
                        pend._x = pe2._x;
                    }

                    var points = "";
                    for (var i = 0; i < pointsArr.length - 1; i++) {
                        points = points + " " + pointsArr[i];
                    }
                    beforepoint = points.trim();

                } else {
                    if (Tools.verdictXY(hotx, hoty, clientX, clientY) < 1) {
                        pend._y = hoty;
                    } else {
                        pend._x = hotx;
                    }
                }

                j = pmove._x + "," + pmove._y + " " + pend._x + "," + pend._y;

                if (action == "Creat") {

                    if (goalEndObj && checkConnect(goalEndObj)) { // 结束
                        hotEndType = getMousePointType(goalEndObj); // 鼠标相对连接结束节点位置计算
                        // 对象长宽
                        var bh = parseInt(converterSVG(goalEndObj, "height"));
                        var bw = parseInt(converterSVG(goalEndObj, "width"));

                        // 节点起始x,y
                        var bx = parseInt(converterSVG(goalEndObj, "left"));
                        var by = parseInt(converterSVG(goalEndObj, "top"));

                        if (hotEndType == "Right") {

                            pend = {
                                _x: bw - (-bx),
                                _y: mousey
                            }
                            pmove._y = mousey;

                        } else if (hotEndType == "Left") {

                            pend = {
                                _x: bx,
                                _y: mousey
                            }
                            pmove._y = mousey;
                        } else if (hotEndType == "Top") {

                            pend = {
                                _x: mousex,
                                _y: by
                            }
                            pmove._x = mousex;
                        } else {

                            pend = {
                                _x: mousex,
                                _y: bh - (-by)
                            }
                            pmove._x = mousex;
                        }

                        freeRulingStatus = false;

                    }

                    j = pmove._x + "," + pmove._y + " " + pend._x + "," + pend._y;

                    if (Tools.isNotBlank(beforepoint)) {
                        j = beforepoint + " " + j;
                        freeLineTemp.points = j;
                    } else {
                        freeLineTemp = {
                            id: "",
                            points: j
                        }
                    }

                    /*if(!freeRulingStatus) {
                        // 直角拐角处理
                        j = route.rightCorner(j);
                    }*/

                }

                return {
                    linePoints: j,
                    lineText: lineText
                }

                break;
            case "Bottom": //上下折线
                j = hotx + "," + hoty + " " + hotx + "," + (hoty + i) + " " + clientX + "," + (hoty + i) + " " + clientX + "," + clientY;
                break;
            case "Bian": //左右折线
                j = hotx + "," + hoty + " " + (hotx + h) + "," + hoty + " " + (hotx + h) + "," + clientY + " " + clientX + "," + clientY;
                break;
            case "Line": // 直线
                j = hotx + "," + hoty + " " + clientX + "," + clientY;
                break;
            case "zhexian": //曲线

                if (hotType == "Bottom" || hotType == "Top") {
                    if (hotType == "Bottom") {
                        k = (g >= 0) ? (Math.abs(g) + k) : k
                    }
                    if (hotType == "Top") {
                        k = (g < 0) ? -(Math.abs(g) + k) : -k
                    }

                    j = hotx + "," + hoty + " " + hotx + "," + (hoty + k) + " " + clientX + "," + (hoty + k) + " " + clientX + "," + clientY;

                } else {
                    if (hotType == "Right") {
                        l = (f > 0) ? (Math.abs(f) + l) : l
                    }
                    if (hotType == "Left") {
                        l = (f < 0) ? -(Math.abs(f) + l) : -l
                    }
                    j = hotx + "," + hoty + " " + (hotx + l) + "," + hoty + " " + (hotx + l) + "," + clientY + " " + clientX + "," + clientY;
                    //j = hotx + "," + hoty + " " + (hotx + l) + "," + hoty + " " + (hotx + l) + "," + hoty2 + " " + hotx2 + "," + hoty2;
                }
                break;
            case "zhexian90": //左右直角
                j = hotx + "," + hoty + " " + clientX + "," + hoty + " " + clientX + "," + clientY;
                break;
            case "zhexian902": //上下直角
                j = hotx + "," + hoty + " " + hotx + "," + clientY + " " + clientX + "," + clientY;
                break

        }

        // 直角拐角处理
        j = route.rightCorner(j);

        return {
            linePoints: j,
            lineText: lineText
        }

    }

    /**
     * 计算线(路由/箭头)的起始位置和结束位置 hotx、hoty、hotx2、hoty2
     * @param {Object} obj  当前鼠标点击的对象，起始、结束节点
     * @param {Object} clientX  相对于客户端X的值(包含滚动条)
     * @param {Object} clientY  相对于客户端Y的值(包含滚动条)
     * @param {String} action   操作类型，有Creat/Edit/NodeMove
     */
    function getObjXY(obj, clientX, clientY, action) {

        if (obj != null) {
            // 节点最右边x，最下边y
            var e = parseInt(converterSVG(obj, "top")) + parseInt(converterSVG(obj, "height")); //offsetHeight height+border
            var f = parseInt(converterSVG(obj, "left")) + parseInt(converterSVG(obj, "width"));

            // 节点起始x,y
            var x = parseInt(converterSVG(obj, "left"));
            var y = parseInt(converterSVG(obj, "top"));

            // 节点中间节点
            var g = x + parseInt(converterSVG(obj, "width") / 2);
            var h = y + parseInt(converterSVG(obj, "height") / 2);

            var i, temphoty; //对应x,y
            var j = "";

            // 用于判断线的起始或落脚点 上下左右
            var k = parseInt(converterSVG(obj, "width") / 4) + 6;
            var l = parseInt(converterSVG(obj, "height") / 4) + 6;

            var m = obj.getAttribute("NodeType");

            // 202003 add by alibao ==============================start
            // 添加对网关连线起始点的矫正 polygon
            var objTagName = obj.tagName;
            // 202003 add by alibao ==============================end

            //offsetX布局移动的x
            //判断点击节点的位置,修改 连线固定连接中间两点
            //console.log("g-k:" + (g - k) + " clientX:" + clientX + " (g + k):" + (g + k) + " (y - l):" + (y - l) + " clientY:" + clientY + " y + l:" + (y + l));
            if ((g - k) < clientX && clientX < (g + k) && (y - l) < clientY && clientY < (y + l)) {

                i = (objTagName != "polygon") ? clientX : g;
                //				i = g;
                temphoty = y;
                j = "Top"
            } else if ((f - k) < clientX && clientX < (f + k) && ((h - l) < clientY && clientY < (h + l))) {
                i = f;
                temphoty = (objTagName != "polygon") ? clientY : h;
                //				temphoty = h;
                j = "Right"
            } else if ((g - k) < clientX && clientX < (g + k) && (e + l) > clientY && clientY > (e - l)) {
                i = (objTagName != "polygon") ? clientX : g;
                //				i = g;
                temphoty = e;
                j = "Bottom"
            } else if ((x - k) < clientX && clientX < (x + k) && ((h - l) < clientY && clientY < (h + l))) {
                i = x;
                temphoty = (objTagName != "polygon") ? clientY : h;
                //				temphoty = h;
                j = "Left"
            } else {
                i = g;
                temphoty = e;
                j = "Bottom"

                if ((StartPointType == "boundaryLine") && Tools.isNotBlank(goalStartObj) && (goalStartObj.id == goalEndObj.id)) { //如果是边界箭头输出
                    j = "outLine";
                }
            }

        } else { //边界箭头
            i = clientX;
            temphoty = clientY;
            j = "";
            if ((StartPointType == "boundaryLine") && Tools.isNotBlank(goalStartObj)) { //如果是边界箭头输出
                j = "outLine";
            }
        }

        if (!action) {
            hotx = i;
            hoty = temphoty;
            hotType = j;

            hotx = hotx - 0 + offsetX; //加滚动条高度
            hoty = hoty - 0 + offsetY;
        } else {
            hotx2 = i;
            hoty2 = temphoty;
            hotEndType = j;

            hotx2 = (hotx2 - 0) + offsetX;
            hoty2 = (hoty2 - 0) + offsetY;
        }

        if (!action) {
            showStartHotPoint(hotx - pointR - offsetX, hoty - pointR - offsetY);
        }

    }

    /**
     * 定义结束连接点的类型  Bottom、Top、Right Left
     * @param {Object} startObj  开始连接点对象
     * @param {Object} endObj    结束连接点对象
     * @param {Object} lineType        线的类型
     */
    function gethotTypeJson(startObj, endObj, lineType) {

        if ((startObj == null || endObj == null) || (startObj.id == endObj.id)) { //startObj, endObj 不为空
            return null;
        }

        var starthotStr = ""; // 开始连接点方向
        var endhotStr = ""; // 结束连接点方向

        // 对象长宽
        var ah = parseInt(converterSVG(startObj, "height"));
        var aw = parseInt(converterSVG(startObj, "width"));
        var bh = parseInt(converterSVG(endObj, "height"));
        var bw = parseInt(converterSVG(endObj, "width"));

        // 节点起始x,y
        var ax = parseInt(converterSVG(startObj, "left"));
        var ay = parseInt(converterSVG(startObj, "top"));
        var bx = parseInt(converterSVG(endObj, "left"));
        var by = parseInt(converterSVG(endObj, "top"));

        // 节点中间节点 x,y
        var axw = parseInt(ax + aw / 2);
        var ayh = parseInt(ay + ah / 2);
        var bxw = parseInt(bx + bw / 2);
        var byh = parseInt(by + bh / 2);

        // 计算结束连接点位置
        if (axw < bxw) {
            if (ayh < byh) { //右下
                starthotStr = "Bottom,Right";
                endhotStr = "Top,Left";
            } else { // 右上
                starthotStr = "Top,Right";
                endhotStr = "Bottom,Left";
            }
        } else {
            if (ayh < byh) { // 左下
                starthotStr = "Bottom,Left";
                endhotStr = "Top,Right";
            } else { // 左上
                starthotStr = "Top,Left";
                endhotStr = "Bottom,Right";
            }
        }

        return {
            starthotStr: starthotStr,
            endhotStr: endhotStr
        }
    }

    /**
     * 计算连接结束节点的提示小圆点
     */
    function calculateEndHotPoint() {

        var clientX = event.clientX; //相对客户端X轴坐标 de
        var clientY = event.clientY;
        document.all.EndNodePoint.style.display = "none";

        for (var i = 0; i < document.all.length; i++) {
            var e = document.all(i);
            var f = e.tagName.toLowerCase();

            if (goalStartObj == null && StartPointType != "boundaryLine" && StartPointType != "freeLine") continue; // 起始节点为空且不是边界箭头也跳过
            if (goalStartObj && goalStartObj.id == e.id) continue; // 起始和结束为同一个节点，跳过

            if ((f == "rect" || f == "path" || f == "circle" || f == "polygon") && !isXYNode(e)) {

                // 节点最右边x，最下边y
                var k = parseInt(converterSVG(e, "top")) + parseInt(converterSVG(e, "height")); //offsetHeight height+border
                var h = parseInt(converterSVG(e, "left")) + parseInt(converterSVG(e, "width"));

                // 节点起始x,y
                var g = parseInt(converterSVG(e, "left"));
                var j = parseInt(converterSVG(e, "top"));

                var endX = 0; // x轴偏移量
                var endY = 0; // y轴偏移量
                if (g < clientX && clientX < h && j < clientY && clientY < k) {
                    var l = getMousePointType(e);

                    switch (l) {
                        case "Bottom":
                            //var m = g + parseInt(converterSVG(e, "width") / 2);
                            endX = clientX;
                            endY = k;
                            break;
                        case "Left":
                            //var n = j + parseInt(converterSVG(e, "height") / 2);
                            endX = g;
                            endY = clientY;
                            break;
                        case "Right":
                            //var n = j + parseInt(converterSVG(e, "height") / 2);
                            endX = h;
                            endY = clientY;
                            break;
                        case "Top":
                            //var m = g + parseInt(converterSVG(e, "width") / 2);
                            endX = clientX;
                            endY = j;
                            break
                    }
                    showEndHotPoint((endX - pointR), (endY - pointR)); //显示连接结束点的提示小圆圈

                }

            }

        }
    }

    /**
     * 获取鼠标相对节点的位置 Top、Right、Bottom、Left
     * @param {Object} obj 节点对象
     */
    function getMousePointType(nodeObj) {

        var f = ""; //位置 Top、Right、Bottom、Left
        var i = event.clientX; //相对客户端X轴坐标 de
        var j = event.clientY;

        // 节点最右边x，最下边y
        var b = parseInt(converterSVG(nodeObj, "top")) + parseInt(converterSVG(nodeObj, "height")); //offsetHeight height+border
        var c = parseInt(converterSVG(nodeObj, "left")) + parseInt(converterSVG(nodeObj, "width"));

        // 节点起始x,y
        var x = parseInt(converterSVG(nodeObj, "left"));
        var y = parseInt(converterSVG(nodeObj, "top"));

        // 节点中间节点
        var d = x + parseInt(converterSVG(nodeObj, "width") / 2);
        var e = y + parseInt(converterSVG(nodeObj, "height") / 2);

        // 判断线边缘面积长度
        var g = parseInt(converterSVG(nodeObj, "width") / 4) + 6;
        var h = parseInt(converterSVG(nodeObj, "height") / 4) + 6;

        if ((d - g) < i && i < (d + g) && (y - h) < j && j < (y + h)) {
            f = "Top"
        } else if ((c - g) < i && i < (c + g) && ((e - h) < j && j < (e + h))) {
            f = "Right"
        } else if ((d - g) < i && i < (d + g) && (b + h) > j && j > (b - h)) {
            f = "Bottom"
        } else if ((x - g) < i && i < (x + g) && ((e - h) < j && j < (e + h))) {
            f = "Left"
        } else {
            f = "Bottom"
        }
        return f
    }

    /**
     * 创建线的函数入口
     * @param {Object} goalTypeName  当下鼠标点击类型
     * @param {Object} clientX       相对客户端x坐标
     * @param {Object} clientY       相对客户端y坐标
     */
    function createLineObj(goalTypeName, clientX, clientY) {

        var endObjTemp = unionNode(goalEndObj);

        switch (goalTypeName) {
            case "PolyLine":

                var startNodeNum = goalStartObj.getAttribute("NodeNum");

                PolyLine1.style.display = "none";
                if (Math.abs(clientX - hotx) <= 5 && Math.abs(clientY - hoty) <= 5)
                    return false;

                if (goalEndObj != null && goalEndObj.tagName != "polyline" && goalEndObj.getAttribute("NodeNum") != startNodeNum && !isXYNode(goalEndObj)) {
                    var d = GetNodeNum("PolyLine");
                    d = "0000" + d;
                    d = d.substring(d.length - 4);
                    if (d.substring(0, 1) != "1") {
                        d = "1" + d
                    }

                    var lineConfig = GetPointsStr("Creat");
                    var f = lineConfig.linePoints;

                    var g = startNodeNum + "_" + goalEndObj.getAttribute("NodeNum");
                    if (g.indexOf("null") != -1 && (StartPointType != "boundaryLine")) {
                        return;
                    }
                    if (/*StartPointType != "innerLine" && */evalbroker("polyline" + g)) {
                        var h = evalbroker("polyline" + g);
                        ShowErrorInfo("节点之间的路由已存在(" + h.getAttribute("Nodeid") + ")!")
                    } else {

                        id = "R" + d;
                        var svgroot = document.getElementById("svg");
                        var labelType = "polyline";

                        /**
                         * SVG 标签属性
                         */
                        var attrNSJSON = {
                            id: "polyline" + g,
                            "fill": "none",
                            "marker-end": "url(#markerEndArrow)",
                            "stroke-width": "1.5",
                            "stroke": "black",
                            "points": f
                        }

                        /**
                         * 自定义属性
                         */
                        var attrJSON = {
                            "Nodeid": id,
                            "SourceNode": goalStartObj.getAttribute("Nodeid"),
                            "TargetNode": goalEndObj.getAttribute("Nodeid"),
                            "LinkeyNode": g,
                            "PolyLineType": StartPointType, //开始 线的形状类型
                            "hotType": hotType,
                            "oldpoints": f,
                            ondblclick: "SetProperty(this, 'Router')"
                        }

                        dcanvas.createSVG(svgroot, labelType, attrNSJSON, attrJSON)

                        var i = f.split(" ");
                        var str1 = i[0].split(",");
                        var str2 = i[1].split(",");
                        var str3 = i[i.length - 2].split(",");
                        var str4 = i[i.length - 1].split(",");

                        attrNSJSON = {
                            id: "polyline" + g + "_text",
                            x: (parseInt(str2[0]) + parseInt(str3[0])) / 2,
                            y: (parseInt(str2[1]) + parseInt(str3[1])) / 2,
                            "font-size": CONFIG.OGFLOW_CONF.nodeFontSize,
                            "text-anchor": "middle",
                            "fill": "black"
                        }

                        attrJSON = {
                            "Nodeid": id + "_text",
                            ondblclick: "SetProperty(this, 'Router')"
                        }

                        labelType = "text";
                        var svgtext = dcanvas.createSVG(svgroot, labelType, attrNSJSON, attrJSON)
                        svgtext.textContent = lineConfig.lineText;
                        RegObj("polyline" + g);

                        // 对边上输出进行排序
                        route.sortSide(goalStartObj, hotType, "LinkeyStartObj");
                        route.sortSide(goalEndObj, hotEndType, "LinkeyEndObj");
                    }
                    goalEndObj = null
                }

                break;
        }
    }

    /**
     * 创建自由折线
     * @param {Object} clientX       相对客户端x坐标
     * @param {Object} clientY       相对客户端y坐标
     * @param {Object} btn           鼠标左键或右键
     */
    function createFreeLine(clientX, clientY, btn) {

        var g = ""; //id的命名规则
        var timeStr = new Date().getTime() + ""; //时间戳

        var endNodeNum = "freeLine_temp_7528";
        if (goalEndObj) {
            endNodeNum = goalEndObj.getAttribute("NodeNum");
        } else {
            endNodeNum = endNodeNum + timeStr.substr((timeStr.length - 4), timeStr.length);
        }

        if (freeLineTemp) {
            var nodeArry = freeLineTemp.id.replace("polyline", "").split("_");
            goalStartObj = evalbroker("Node" + nodeArry[0]);
        }
        g = goalStartObj.getAttribute("NodeNum") + "_" + endNodeNum;

        var lineConfig = GetPointsStr("Creat");
        var f = lineConfig.linePoints;

        // 把原先存在的线给删除了
        if (freeLineTemp && Tools.isNotBlank(freeLineTemp.id) && evalbroker(freeLineTemp.id)) {
            deleteobj(evalbroker(freeLineTemp.id));
        }

        if (g.indexOf("freeLine_temp_7528") == -1 && evalbroker("polyline" + g)) {
            var h = evalbroker("polyline" + g);
            cancelFreeLine();
            ShowErrorInfo("节点之间的路由已存在(" + h.getAttribute("Nodeid") + ")!")
        }

        var d = GetNodeNum("PolyLine");
        d = "0000" + d;
        d = d.substring(d.length - 4);
        if (d.substring(0, 1) != "1") {
            d = "1" + d
        }

        id = "R" + d;
        var svgroot = document.getElementById("svg");
        var labelType = "polyline";
        // 目标节点
        if (!freeRulingStatus) {
            var TargetNode = goalEndObj.getAttribute("Nodeid");
        } else {
            TargetNode = "";
        }

        /**
         * SVG 标签属性
         */
        var attrNSJSON = {
            id: "polyline" + g,
            fill: "none",
            //			"marker-end": "url(#markerEndArrow)",
            "stroke-width": "1.5",
            "stroke": "black",
            "points": f
        }

        /**
         * 自定义属性
         */
        var attrJSON = {
            "Nodeid": id,
            //							"SourceNode": goalStartObj.getAttribute("Nodeid"),
            "TargetNode": TargetNode,
            "LinkeyNode": g,
            "PolyLineType": StartPointType, //开始 线的形状类型
            "hotType": hotType,
            "oldpoints": f,
            ondblclick: "SetProperty(this, 'Router')"
        }

        var newLineObj = dcanvas.createSVG(svgroot, labelType, attrNSJSON, attrJSON)

        var i = f.split(" ");
        var str1 = i[0].split(",");
        var str2 = i[1].split(",");
        var str3 = i[i.length - 2].split(",");
        var str4 = i[i.length - 1].split(",");

        attrNSJSON = {
            id: "polyline" + g + "_text",
            x: (parseInt(str2[0]) + parseInt(str3[0])) / 2,
            y: (parseInt(str2[1]) + parseInt(str3[1])) / 2,
            "font-size": CONFIG.OGFLOW_CONF.nodeFontSize,
            "text-anchor": "middle",
            fill: "black"
        }

        attrJSON = {
            "Nodeid": id + "_text",
            ondblclick: "SetProperty(this, 'Router')"
        }

        labelType = "text";
        var svgtext = dcanvas.createSVG(svgroot, labelType, attrNSJSON, attrJSON)
        svgtext.textContent = lineConfig.lineText;

        // 取消划线状态
        if (!freeRulingStatus) {

            newLineObj.setAttributeNS(null, "marker-end", "url(#markerEndArrow)");
            newLineObj.setAttribute("SourceNode", goalStartObj.getAttribute("Nodeid"));
            RegObj("polyline" + g);

            // 对边上输出进行排序
            //			route.sortSide(goalStartObj, hotType, "LinkeyStartObj");
            //			route.sortSide(goalEndObj, hotEndType, "LinkeyEndObj");

            cancelFreeLine();

        } else {
            freeLineTemp.id = "polyline" + g;
        }

    }

    /**
     * 自由折线，取消划线状态
     */
    function cancelFreeLine() {

        PolyLine1.style.display = "none";
        var p1 = document.getElementById("PolyLine1");
        p1.setAttribute("points", "0,0 1,1");

        if (freeLineTemp && Tools.isNotBlank(freeLineTemp.id) && evalbroker(freeLineTemp.id)) {
            deleteobj(evalbroker(freeLineTemp.id));
        }

        freeLineTemp = null;
        goalStartObj = null;
        goalEndObj = null;
        freeRulingStatus = false;
    }

    /**
     * 创建边界箭头
     * @param {Object} goalTypeName  当下鼠标点击类型
     * @param {Object} clientX       相对客户端x坐标
     * @param {Object} clientY       相对客户端y坐标
     */
    function createBoundaryLine(goalTypeName, clientX, clientY) {
        switch (goalTypeName) {
            case "PolyLine":
                var g = ""; //id的命名规则
                var timeStr = new Date().getTime() + ""; //时间戳
                var startNodeNum = timeStr.substr((timeStr.length - 4), timeStr.length);

                if (goalStartObj) {
                    g = goalStartObj.getAttribute("NodeNum") + "_" + startNodeNum;
                } else {
                    g = startNodeNum + "_" + goalEndObj.getAttribute("NodeNum");
                }

                var lineConfig = GetPointsStr("Creat");
                var f = lineConfig.linePoints;

                if (Tools.isBlank(f)) return;

                var d = GetNodeNum("PolyLine");
                d = "0000" + d;
                d = d.substring(d.length - 4);
                if (d.substring(0, 1) != "1") {
                    d = "1" + d
                }

                id = "R" + d;
                var svgroot = document.getElementById("svg");
                var labelType = "polyline";
                // 目标节点
                var TargetNode = "";
                if (goalEndObj != null) {
                    TargetNode = goalEndObj.getAttribute("Nodeid");
                    if (goalStartObj != null && goalStartObj.id == goalEndObj.id) {
                        goalEndObj = "";
                    }
                }

                /**
                 * SVG 标签属性
                 */
                var attrNSJSON = {
                    id: "polyline" + g,
                    fill: "none",
                    "marker-end": "url(#markerEndArrow)",
                    "stroke-width": "1.5",
                    "stroke": "black",
                    "points": f
                }

                /**
                 * 自定义属性
                 */
                var attrJSON = {
                    "Nodeid": id,
                    //							"SourceNode": goalStartObj.getAttribute("Nodeid"),
                    "TargetNode": TargetNode,
                    "LinkeyNode": g,
                    "PolyLineType": StartPointType, //开始 线的形状类型
                    "hotType": hotType,
                    "oldpoints": f,
                    ondblclick: "SetProperty(this, 'Router')"
                }

                dcanvas.createSVG(svgroot, labelType, attrNSJSON, attrJSON)

                var i = f.split(" ");
                var str1 = i[0].split(",");
                var str2 = i[1].split(",");
                var str3 = i[i.length - 2].split(",");
                var str4 = i[i.length - 1].split(",");

                attrNSJSON = {
                    id: "polyline" + g + "_text",
                    x: (parseInt(str2[0]) + parseInt(str3[0])) / 2,
                    y: (parseInt(str2[1]) + parseInt(str3[1])) / 2,
                    "font-size": CONFIG.OGFLOW_CONF.nodeFontSize,
                    "text-anchor": "middle",
                    fill: "black"
                }

                attrJSON = {
                    "Nodeid": id + "_text",
                    ondblclick: "SetProperty(this, 'Router')"
                }

                labelType = "text";
                var svgtext = dcanvas.createSVG(svgroot, labelType, attrNSJSON, attrJSON)
                svgtext.textContent = lineConfig.lineText;
                RegObj("polyline" + g);

                // 对边上输出进行排序
                if (Tools.isBlank(goalStartObj)) {
                    route.sortSide(goalEndObj, hotEndType, "LinkeyEndObj");
                } else {
                    route.sortSide(goalStartObj, hotType, "LinkeyStartObj");
                }

                break;
        }
    }

    /**
     * 移动线的某一段
     * @param {Object} lineObj
     * @param {Object} x   鼠标x坐标
     * @param {Object} y   鼠标y坐标
     */
    function MoveLine(lineObj, x, y) {

        var lineType = lineObj.getAttribute("PolyLineType");

        // 内部箭头或边界箭头
        if (lineType == "boundaryLine" || lineType == "innerLine" || lineType == "freeLine") {
            route.moveSmartLinePart(lineObj, x, y);
        } else { // 原生路由处理
            route.moveLinePart(lineObj, x, y);
        }

    }

    /**
     * 重新编写线条移动的相对位置，使用自定义属性sratio、eratio
     * @param {Object} a 移动的节点对象
     * @param {Object} x x的偏移量
     * @param {Object} y y轴偏移量
     */
    function toMoveLine(a, x, y) {

        if (a != undefined && a != null) {

            var gh = route.getOneNodeWithLines(a);

            if (gh != "") {
                var j = gh.split(",");

                for (var i = 0; i < j.length; i++) {

                    var lineObj = evalbroker(j[i]);
                    var oo = lineObj.id.replace("polyline", "").split("_");
                    var hotType = lineObj.getAttribute("hotType");
                    var r = lineObj.getAttribute("PolyLineType");

                    var sratio = lineObj.getAttribute("sratio");
                    var eratio = lineObj.getAttribute("eratio");

                    var sobj = evalbroker("Node" + oo[0]);
                    var eobj = evalbroker("Node" + oo[1]);

                    // console.log(lineObj.id + "=== sratio: " + sratio + "   eratio: " + eratio);

                    // 边界箭头处理
                    if (r == "boundaryLine") {
                        // 边界箭头移动时处理
                        toRedrawBoundaryLine(lineObj, sobj, eobj);

                    } else if (r == "innerLine") {
                        toRedrawInnerLine(lineObj, sobj, eobj);

                    } else if (r == "freeLine") {

                        toRedrawFreeLine(lineObj, sobj, eobj);

                    } else {
                        // var hotEndType = getEndType(sobj, eobj, hotType, r);
                        var startp = getLocationnew(sobj, sratio, lineObj);
                        var endp = getLocationnew(eobj, eratio, lineObj);

                        // console.log("startp::" + startp + "  endp::" + endp);
                        toRedraw(lineObj, r, hotType, startp, endp);
                    }
                }
            }

        } else {
            var svgobj = document.getElementById("svg");
            for (var i = 0; i < svgobj.childNodes.length; i++) {
                var lineObj = svgobj.childNodes[i];
                if (lineObj.tagName == "polyline" && !isAssist(lineObj)) {

                    var oo = lineObj.id.replace("polyline", "").split("_");
                    var hotType = lineObj.getAttribute("hotType");
                    var r = lineObj.getAttribute("PolyLineType");
                    var sratio = lineObj.getAttribute("sratio");
                    var eratio = lineObj.getAttribute("eratio");

                    //console.log("sratio::" + sratio + "  eratio::" + eratio);

                    var sobj = evalbroker("Node" + oo[0]);
                    var eobj = evalbroker("Node" + oo[1]);

                    // 边界箭头处理
                    if (r == "boundaryLine") {
                        // 边界箭头移动时处理
                        toRedrawBoundaryLine(lineObj, sobj, eobj);

                    } else if (r == "innerLine") {
                        toRedrawInnerLine(lineObj, sobj, eobj);

                    } else if (r == "freeLine") {
                        //						toRedrawFreeLine(lineObj, sobj, eobj);
                        toRedrawInnerLine(lineObj, sobj, eobj);
                    } else {

                        var startp = getLocationnew(sobj, sratio, lineObj);
                        var endp = getLocationnew(eobj, eratio, lineObj);

                        //console.log("startp::" + startp + "  endp::" + endp);
                        toRedraw(lineObj, r, hotType, startp, endp);
                    }
                }
            }
        }
    }

    /**
     * 自由折线的移动【移动单个节点】
     * @param {Object} lineObj
     * @param {Object} sobj
     * @param {Object} eobj
     */
    function toRedrawFreeLine(lineObj, sobj, eobj) {

        var sratio = lineObj.getAttribute("sratio");
        var eratio = lineObj.getAttribute("eratio");
        var startp = getLocationnew(sobj, sratio, lineObj);
        var endp = getLocationnew(eobj, eratio, lineObj);

        var lx = startp.split(",")[0] - 0;
        var ly = startp.split(",")[1] - 0;
        var lex = endp.split(",")[0] - 0;
        var ley = endp.split(",")[1] - 0;

        var hotPointType = route.getPointType(lx, ly, sobj) // 鼠标相对连接结束节点位置计算
        var endPointType = route.getPointType(lex, ley, eobj)

        //				console.log(lineObj.id + "==>" + startp + "  " + endp);

        var difsx = lx; // 连接点切换时起始值
        var difsy = ly;

        var h = (lex - lx) / 2;
        var i = (ley - ly) / 2;

        // 对象长宽
        var ah = parseInt(converterSVG(sobj, "height"));
        var aw = parseInt(converterSVG(sobj, "width"));
        var bh = parseInt(converterSVG(eobj, "height"));
        var bw = parseInt(converterSVG(eobj, "width"));

        // 对象 xy
        // 节点起始x,y
        var ax = parseInt(converterSVG(sobj, "left"));
        var ay = parseInt(converterSVG(sobj, "top"));
        var bx = parseInt(converterSVG(eobj, "left"));
        var by = parseInt(converterSVG(eobj, "top"));

        var lineArr = lineObj.getLinePointsArr();
        if (moveobj.id == sobj.id) {

            h = (lineArr[3].x - lx) / 2;
            i = (lineArr[3].y - ly) / 2;
            if (hotPointType == "Right" || hotPointType == "Left") {
                lineArr[0] = {
                    x: lx,
                    y: ly
                }
                lineArr[1] = {
                    x: lx - (-h),
                    y: ly
                }
                lineArr[2] = {
                    x: lx - (-h),
                    y: lineArr[3].y
                }
            } else {
                lineArr[0] = {
                    x: lx,
                    y: ly
                }
                lineArr[1] = {
                    x: lx,
                    y: ly - (-i)
                }
                lineArr[2] = {
                    x: lineArr[3].x,
                    y: ly - (-i)
                }
            }

        } else {

            var length = lineArr.length;
            h = (lex - lineArr[length - 4].x) / 2;
            i = (ley - lineArr[length - 4].y) / 2;
            if (endPointType == "Right" || endPointType == "Left") {
                lineArr[length - 1] = {
                    x: lex,
                    y: ley
                }
                lineArr[length - 2] = {
                    x: lex - h,
                    y: ley
                }
                lineArr[length - 3] = {
                    x: lex - h,
                    y: lineArr[length - 4].y
                }
            } else {
                lineArr[length - 1] = {
                    x: lex,
                    y: ley
                }
                lineArr[length - 2] = {
                    x: lex,
                    y: ley - i
                }
                lineArr[length - 3] = {
                    x: lineArr[length - 4].x,
                    y: ley - i
                }
            }

        }

        lineObj.setPoints(lineArr);

        // 直角拐角处理
        //		j = route.rightCorner(j);

        //		lineObj.setAttribute("oldpoints", j);
        //		lineObj.setAttribute("orpoints", j);
        //		lineObj.setAttribute("points", j);
        //		AddRouterText(lineObj, "MoveObj")
    }

    /**
     * 处理内部箭头的移动【节点移动线移动】
     * @param {Object} lineObj
     * @param {Object} sobj 起始节点
     * @param {Object} eobj 结束节点
     * @param {Object} hotPointType
     */
    function toRedrawInnerLine(lineObj, sobj, eobj) {

        var hotJson = gethotTypeJson(sobj, eobj, "innerLine");

        var starthot1 = "";
        var endhot1 = "";

        var sratio = lineObj.getAttribute("sratio");
        var eratio = lineObj.getAttribute("eratio");
        var startp = getLocationnew(sobj, sratio, lineObj);
        var endp = getLocationnew(eobj, eratio, lineObj);

        var lx = startp.split(",")[0] - 0;
        var ly = startp.split(",")[1] - 0;
        var lex = endp.split(",")[0] - 0;
        var ley = endp.split(",")[1] - 0;

        var hotPointType = route.getPointType(lx, ly, sobj) // 鼠标相对连接结束节点位置计算
        var endPointType = route.getPointType(lex, ley, eobj)

        // console.log(lineObj.id + "==>" + startp + "  " + endp);
        // console.log(sobj.id + "=s=>" + hotPointType);
        // console.log(eobj.id + "=e=>" + endPointType);

        var difsx = lx; // 连接点切换时起始值
        var difsy = ly;

        var h = (lex - lx) / 2;
        var i = (ley - ly) / 2;

        // 对象长宽
        var ah = parseInt(converterSVG(sobj, "height"));
        var aw = parseInt(converterSVG(sobj, "width"));
        var bh = parseInt(converterSVG(eobj, "height"));
        var bw = parseInt(converterSVG(eobj, "width"));

        // 对象 xy
        // 节点起始x,y
        var ax = parseInt(converterSVG(sobj, "left"));
        var ay = parseInt(converterSVG(sobj, "top"));
        var bx = parseInt(converterSVG(eobj, "left"));
        var by = parseInt(converterSVG(eobj, "top"));

        if (Tools.isNotBlank(hotJson)) {
            starthot1 = hotJson.starthotStr;
            endhot1 = hotJson.endhotStr;

            //			console.log("starthot1: " + starthot1 + "  endhot1: " + endhot1);
            //			console.log("ax: " + ax + ", ay: " + ay + "  bx: " + bx + ", by: " + by);

            // 开始连接点
            /*if(Tools.isNotBlank(hotPointType) && starthot1.indexOf(hotPointType) == -1) {
                if(hotPointType == "Left") {
                    difsx = parseInt(lx + aw);
                } else if(hotPointType == "Right") {
                    difsx = parseInt(lx - aw);
                } else if(hotPointType == "Bottom") {
                    difsy = parseInt(ly - ah);
                } else if(hotPointType == "Top") {
                    difsy = parseInt(ly + ah);
                }
            }
            // 结束连接点
            if(Tools.isNotBlank(endPointType) && endhot1.indexOf(endPointType) == -1) {

                if(endPointType == "Left") {
                    lex = parseInt(lex - bw);
                } else if(endPointType == "Right") {
                    lex = parseInt(lex + bw);
                } else if(endPointType == "Bottom") {
                    ley = parseInt(ley - bh);
                } else if(endPointType == "Top") {
                    ley = parseInt(ley + bh);
                }
                h = (lex - difsx) / 2;
                i = (ley - difsy) / 2;
            }*/
        }

        //		console.log("h:" + h + "  i: " + i);
        var p = CONFIG.LINE.curveL;
        var q = CONFIG.LINE.curveL;
        if ((hotPointType == "Bottom" || hotPointType == "Top") && (hotPointType == endPointType) && Math.abs(by - ay) < bh) {

            if (hotPointType == "Bottom") {
                p = (ley <= ly) ? (ly + p) : (ley + p);
            }
            if (hotPointType == "Top") {
                p = (ley <= ly) ? (ley - p) : (ly - p);
            }
            j = difsx + "," + difsy + " " + difsx + "," + p + " " + lex + "," + p + " " + lex + "," + ley;

        } else if ((hotPointType == "Right" || hotPointType == "Left") && (hotPointType == endPointType) && Math.abs(bx - ax) < bw) {
            if (hotPointType == "Right") {
                q = (lex <= lx) ? (lx + q) : (lex + q);
            }
            if (hotPointType == "Left") {
                q = (lex <= lx) ? (lex - q) : (lx - q);
            }
            j = difsx + "," + difsy + " " + q + "," + difsy + " " + q + "," + ley + " " + lex + "," + ley;
            console.log(j);
        } else {
            if (hotPointType == "Bottom" || hotPointType == "Top") { //开始为上线
                j = difsx + "," + difsy + " " + difsx + "," + (difsy + i) + " " + lex + "," + (difsy + i) + " " + lex + "," + ley;

                //			if(lex > ax && lex < parseInt(ax + aw)) { //中间为直线
                //				j = lex + "," + difsy + " " + lex + "," + ley;
                //			}
                if (endPointType == "Right" || endPointType == "Left") { //结束为左右
                    j = difsx + "," + difsy + " " + difsx + "," + ley + " " + lex + "," + ley;
                }

            } else {
                j = difsx + "," + difsy + " " + (difsx + h) + "," + difsy + " " + (difsx + h) + "," + ley + " " + lex + "," + ley;
                //			if(ley > ay && ley < parseInt(ay + ah)) { //中间为直线
                //				j = difsx + "," + ley + " " + lex + "," + ley;
                //			}
                if (endPointType == "Bottom" || endPointType == "Top") { //结束为上下
                    j = difsx + "," + difsy + " " + lex + "," + difsy + " " + lex + "," + ley;
                }
            }
        }

        // 直角拐角处理
        j = route.rightCorner(j);

        lineObj.setAttribute("oldpoints", j);
        lineObj.setAttribute("orpoints", j);
        lineObj.setAttribute("points", j);
        AddRouterText(lineObj, "MoveObj")

    }

    /**
     * 边界箭头移动 【节点移动线移动】
     * @param {Object} lineObj 边界箭头对象
     * @param {Object} sobj 起始节点
     * @param {Object} eobj 结束节点
     */
    function toRedrawBoundaryLine(lineObj, sobj, eobj) {

        var points = lineObj.getAttribute("points");
        var pointArr = points.split(" ");

        var lx = pointArr[0].split(",")[0] - 0;
        var ly = pointArr[0].split(",")[1] - 0;
        var lex = pointArr[pointArr.length - 1].split(",")[0] - 0;
        var ley = pointArr[pointArr.length - 1].split(",")[1] - 0;

        var sratio = lineObj.getAttribute("sratio");
        var eratio = lineObj.getAttribute("eratio");

        var difx = lex - lx;
        var dify = ley - ly;

        if (Tools.isNotBlank(eobj)) {
            var endp = getLocationnew(eobj, eratio, lineObj);
            lex = endp.split(",")[0] - 0;
            ley = endp.split(",")[1] - 0;

            points = (lex - difx) + "," + (ley - dify) + " " + lex + "," + ley;

            var startEndConfig = CONFIG.LINE.startEndConfig;
            if (ly == ley && startEndConfig.flag) {
                points = startEndConfig.startx + "," + (ley - dify) + " " + lex + "," + ley;
            }
        } else if (Tools.isNotBlank(sobj)) {
            var startp = getLocationnew(sobj, sratio, lineObj);
            lx = startp.split(",")[0] - 0;
            ly = startp.split(",")[1] - 0;

            points = lx + "," + ly + " " + (lx + difx) + "," + (ly + dify);

            var startEndConfig = CONFIG.LINE.startEndConfig;
            if (ly == ley && startEndConfig.flag) {
                points = points = lx + "," + ly + " " + startEndConfig.endx + "," + (ly + dify);
            }
        }

        // 直角拐角处理
        points = route.rightCorner(points);

        //				console.log(points);
        lineObj.setAttribute("oldpoints", points);
        lineObj.setAttribute("orpoints", points);
        lineObj.setAttribute("points", points);
        AddRouterText(lineObj, "MoveObj")
    }

    /**
     * 移动时重绘划线函数
     * @param {Object} b  线的DOM对象
     * @param {Object} r  线的类型
     * @param {Object} hotType
     * @param {Object} startp  开始点
     * @param {Object} endp    结束点
     */
    function toRedraw(b, r, hotType, startp, endp) {
        try {
            var lx = startp.split(",")[0] - 0;
            var ly = startp.split(",")[1] - 0;
            var lex = endp.split(",")[0] - 0;
            var ley = endp.split(",")[1] - 0;

            var p = 30;
            var q = 30;
            var t = "";

            switch (r) {

                case "Bottom":
                    var u = ly + (ley - ly) / 2;
                    if (b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") u = parseInt(b.getAttribute("TopY"));
                    t = startp + " " + lx + "," + u + " " + lex + "," + u + " " + endp;
                    break;
                case "Bian":
                    var u = lx + (lex - lx) / 2;
                    if (b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") u = parseInt(b.getAttribute("TopY"));
                    t = startp + " " + u + "," + ly + " " + u + "," + ley + " " + endp;
                    break;
                case "Line":
                    t = startp + " " + endp;
                    break;
                case "zhexian":
                    switch (hotType) {
                        case "Bottom":
                            p = (ley <= ly) ? (ly + p) : (ley + p);
                            if (b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") p = parseInt(b.getAttribute("TopY"));
                            t = startp + " " + lx + "," + p + " " + lex + "," + p + " " + endp;
                            break;
                        case "Top":
                            p = (ley <= ly) ? (ley - p) : (ly - p);
                            if (b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") p = parseInt(b.getAttribute("TopY"));
                            t = startp + " " + lx + "," + p + " " + lex + "," + p + " " + endp;
                            break;
                        case "Right":
                            q = (lex <= lx) ? (lx + q) : (lex + q);
                            if (b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") q = parseInt(b.getAttribute("TopY"));
                            t = startp + " " + q + "," + ly + " " + q + "," + ley + " " + endp;
                            break;
                        case "Left":
                            q = (lex <= lx) ? (lex - q) : (lx - q);
                            if (b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") q = parseInt(b.getAttribute("TopY"));
                            t = startp + " " + q + "," + ly + " " + q + "," + ley + " " + endp;
                            break
                    }
                    break;
                case "zhexian90":
                    t = startp + " " + lex + "," + ly + " " + endp;
                    break;
                case "zhexian902":
                    t = startp + " " + lx + "," + ley + " " + endp;
                    break
            }
            b.setAttribute("oldpoints", t);
            b.setAttribute("orpoints", t);
            b.setAttribute("points", t);
            AddRouterText(b, "MoveObj")
        } catch (E) {
            ShowErrorInfo("本活动出错,请执行错误修复操作!")
        }

    }

    /**
     * 校验鼠标在节点旁边是否有效
     * @param {Object} nodeObj svg图像节点对象
     */
    function checkConnect(nodeObj) {

        if (!nodeObj) {
            return false;
        }

        var x = converterSVG(nodeObj, "left") - 0;
        var y = converterSVG(nodeObj, "top") - 0;
        var w = converterSVG(nodeObj, "width") - 0;
        var h = converterSVG(nodeObj, "height") - 0;
        var ox = event.clientX - 0;
        var oy = event.clientY - 0;

        if (ox < (x - 4) || ox > (x + w + 4) || oy < (y - 4) || oy > (y + h + 4))
            return false;
        else
            return true;
    }

    /**
     * 节点文字换行处理
     * @param {String} nodeId  节点ID
     * @param {String} textValue  文本的值
     * <foreignObject width="{width}" height="{height}" x="{textX}" y="textY" style="text-align: center;" my-index="8">  <p aligin="center" style="line-height: 25px; font-size:12px; margin: 0 2px;">{textValue}</p> </foreignObject>
     */
    function textLineFeed(nodeId, textValue) {

        if (Tools.isBlank(textValue)) {

            try {
                /*$("#" + nodeId + "_text").css("display", "");
                $("#" + nodeId + "_text_more").css("display", "none");
                return;*/
                if (nodeId.indexOf("_text_more") != -1) {
                    nodeId = nodeId.substring(0, nodeId.indexOf("_text_more"));
                }
                textValue = $("#" + nodeId + "_text").text();
            } catch (e) {
            }
        }

        var nodeObj = document.getElementById(nodeId);
        var ondblclick = nodeObj.getAttribute("ondblclick");
        // 如果是圆形 开始，结束，事件，则返回
        if (nodeObj.tagName == "circle") {
            $("#" + nodeId + "_text").css("display", "");
            return;
        }

        var textValue1, x1, y1; // 定义换行文字位置
        var textValue2, x2, y2;

        // 计算2行数文本位置
        var tsapnConfig = calculateTSpanXY(nodeId, textValue);

        var svgroot = document.getElementById("svg");

        // text和tspan使用
        try {
            $("#" + nodeId + "_text").css("display", "none"); // 将原先的隐藏

            var textSpan = document.getElementById(nodeId + "_text_more");

            if (Tools.isNotBlank(textSpan)) {
                textSpan.parentNode.removeChild(textSpan);
            }

            textSpan = svg.create("text").attrNS({
                id: nodeId + "_text_more",
                "font-size": CONFIG.OGFLOW_CONF.nodeFontSize
            }).attr({
                "my-index": "8",
            }).appendTo(svgroot).node;

            var tspan1 = svg.create("tspan").attrNS({
                x: tsapnConfig.x1,
                y: tsapnConfig.y1
            }).attr({
                Nodeid: nodeObj.getAttribute("Nodeid"),
                ondblclick: ondblclick
            }).appendTo(textSpan).node;
            tspan1.textContent = tsapnConfig.textValue1;

            var tspan2 = svg.create("tspan").attrNS({
                x: tsapnConfig.x2,
                y: tsapnConfig.y2
            }).attr({
                Nodeid: nodeObj.getAttribute("Nodeid"),
                ondblclick: ondblclick
            }).appendTo(textSpan).node;
            tspan2.textContent = tsapnConfig.textValue2;

        } catch (e) {
        }

    }

    /**
     * 获取字符串长度（汉字算两个字符，字母数字算一个）
     * @param val  字符串
     * @returns {number} 返回长度
     */
    function getByteLen(val) {
        var len = 0;
        for (var i = 0; i < val.length; i++) {
            var a = val.charAt(i);
            if (a.match(/[^\x00-\xff]/ig) != null) {
                len += 2;
            } else {
                len += 1;
            }
        }
        return len;
    }

    /**
     * 计算换行时文字节点的位置
     * @param {Object} nodeId    环节节点id
     * @param {Object} textValue 节点文本内容
     */
    function calculateTSpanXY(nodeId, textValue) {

        var nodeObj = document.getElementById(nodeId);

        offsetX = parseInt(document.body.scrollLeft);
        offsetY = parseInt(document.body.scrollTop);

        // 节点起始x,y
        var x = parseInt(converterSVG(nodeObj, "left")) - (-offsetX);
        var y = parseInt(converterSVG(nodeObj, "top")) - (-offsetY);
        // 高和宽
        var width = parseInt(converterSVG(nodeObj, "width"));
        var height = parseInt(converterSVG(nodeObj, "height"));

        //		alert("x: " + x + ", y: " + y + "  w: " + width + ", h: " + height);

        //9 12px
        var nodeFontSize = CONFIG.OGFLOW_CONF.nodeFontSize;
        var nodeFontSize = nodeFontSize.trim().substring(0, nodeFontSize.length - 2);
        var fontSize = parseInt(nodeFontSize) * 4 / 3;
        var lineSize = parseInt(width / fontSize) - 1; // 每行字数

        var textValue1 = "",
            x1 = 0,
            y1 = 0; // 定义换行文字位置
        var textValue2 = "",
            x2 = 0,
            y2 = 0;

        var diffx = 0; //水平居中时候使用
        var diffy = 0; //垂直居中时使用

        var textLength = parseInt(getByteLen(textValue));

        switch (nodeObj.tagName) {

            case "rect":
                if (textValue.length >= lineSize) {
                    diffx = fontSize / 2;
                    diffy = 6 + fontSize;
                    textValue1 = textValue.substring(0, lineSize);
                    if (textValue.length < (2 * lineSize)) {
                        textValue2 = textValue.substring(lineSize, textValue.length);
                    } else {
                        textValue2 = textValue.substring(lineSize, (2 * lineSize) - 1) + "…"
                    }
                } else {
                    textValue1 = textValue.substring(0, textValue.length);
                    diffx = (width - fontSize * textValue1.length) / 2;
                    diffy = (height + fontSize - 1) / 2 - 1;
                }

                x1 = x2 = (x + diffx);
                y1 = (y + diffy);
                y2 = (y + 8 + diffy + fontSize);

                break;
            case "polygon":

                lineSize = lineSize - 1;
                if (textValue.length >= lineSize) {
                    diffx = fontSize;
                    textValue1 = textValue.substring(0, (lineSize - 1)) + "…";
                } else {
                    textValue1 = textValue.substring(0, textValue.length);
                    diffx = (width - fontSize * textValue1.length) / 2;
                }
                diffy = (height + fontSize - 1) / 2;

                x1 = x2 = (x + diffx);
                y1 = y + diffy;

                break;
            case "path":
                lineSize = lineSize - 1;
                if (textValue.length >= lineSize) {
                    diffx = fontSize;
                    diffy = 6 + fontSize;
                    textValue1 = textValue.substring(0, lineSize);
                    if (textValue.length < (2 * lineSize)) {
                        textValue2 = textValue.substring(lineSize, textValue.length);
                    } else {
                        textValue2 = textValue.substring(lineSize, (2 * lineSize) - 1) + "…"
                    }
                } else {
                    textValue1 = textValue.substring(0, textValue.length);
                    diffx = (width - fontSize * textValue1.length) / 2;
                    diffy = (height + fontSize - 1) / 2 - 1;
                }

                x1 = x2 = (x + diffx);
                y1 = (y + diffy);
                y2 = (y + 8 + diffy + fontSize);

                break;

        }

        var tspanConfig = {
            textValue1: textValue1,
            x1: x1,
            y1: y1,
            textValue2: textValue2,
            x2: x2,
            y2: y2
        }

        return tspanConfig
    }

    /**
     * 修改大小变换的函数
     * @param {Object} nodeObj 修改的对象
     */
    function editArea(nodeObj) {

        if (nodeObj.getAttribute("NodeType") == 'YNode_text' || nodeObj.getAttribute("NodeType") == 'XNode_text' || nodeObj.tagName == "polyline") {
            return;
        }

        var b = parseInt(document.body.scrollLeft);
        var c = parseInt(document.body.scrollTop);
        var d = getEventOX(event);
        var e = getEventOY(event);
        var ax = parseInt(converterSVG(nodeObj, "left"));
        var ay = parseInt(converterSVG(nodeObj, "top"));
        var w = d - ax;
        var h = e - ay;

        if (w < 30) {
            w = 30
        }
        if (h < 30) {
            h = 20
        }

        // 202003 add by alibao ===============start
        // 添加xy泳道放大缩小处理
        if (nodeObj.getAttribute("NodeType") == "XNode" || nodeObj.getAttribute("NodeType") == "YNode") {

            var nodeId = nodeObj.id;
            var xyNum = nodeId.substring(nodeId.indexOf("Node") + 4);
            var nodeJ = "Node" + (xyNum - 0 + 10000);
            var nodet = "Node" + xyNum + "_xtb"

            var nodeJobj = document.getElementById(nodeJ);
            var nodeTextObj = document.getElementById(nodet);
            var texty = nodeTextObj.getBBox().height / 2;

            var nodeax = parseInt(converterSVG(nodeJobj, "left")) - 0 + b;
            var nodeay = parseInt(converterSVG(nodeJobj, "top")) - 0 + c;
            var boxx = nodeJobj.getBBox().width / 2;
            var boxy = nodeJobj.getBBox().height / 2;

            if (boxy > texty) {
                texty = boxy - texty;
            } else {
                texty = 0;
            }


            nodeObj.setAttribute("width", w);
            nodeObj.setAttribute("height", h);

            if (nodeObj.getAttribute("NodeType") == "XNode") {
                nodeJobj.setAttribute("height", e - nodeay);
                nodeTextObj.setAttribute("y", nodeay - 0 + texty);
            } else {
                nodeJobj.setAttribute("width", d - nodeax);
                nodeTextObj.setAttribute("x", nodeax - 0 + boxx);
            }

        }
        // 202003 add by alibao ===============end


        if ("rect" == nodeObj.tagName && !isXYNode(nodeObj)) {
            nodeObj.setAttribute("width", w);
            nodeObj.setAttribute("height", h);
        } else if ("circle" == nodeObj.tagName) {
            nodeObj.setAttribute("r", h / 2);
        } else if (nodeObj.tagName == "path") {
            if ("SubProcess" == nodeObj.getAttribute("NodeType")) {
                var d1 = w - 0.1 * w;
                var str = "M0 0 L" + w + " 0 L" + w + " " + h + " L" + d1 + " " + h + " L" + d1 + " 0 L" + d1 + " " + h + " L" + 0.1 * w + " " + h + " L" + 0.1 * w + " 0" + " L" + 0.1 * w + " " + h + " L0 " + h + " Z";
                nodeObj.setAttribute("d", str);
            } else if ("OutProcess" == nodeObj.getAttribute("NodeType")) {
                var str = "M" + 0.1 * w + " 0 L" + (w - 0.1 * w) + " 0 A" + 0.1 * w + " " + 0.1 * w + ",0,0,0," + w + " " + 0.1 * w + " L" + w + " " + (h - 0.1 * w) + " A" + 0.1 * w + " " + 0.1 * w + ",0,0,0" + (w - 0.1 * w) + " " + h + " L" + 0.1 * w + " " + h + " A" + 0.1 * w + " " + 0.1 * w + ",0,0,0,0 " + (h - 0.1 * w) + " L0 " + 0.1 * w + " A" + 0.1 * w + " " + 0.1 * w + ",0,0,0," + 0.1 * w + " 0Z";
                nodeObj.setAttribute("d", str);
            }
        }
        if (nodeObj.tagName == "polygon") {
            h = h * 2;
            nodeObj.setAttribute("points", (d - w / 2 + offsetX) + "," + (e - h / 2 + offsetY) + " " + (d + offsetX) + "," + (e + offsetY) + " " + (d - w / 2 + offsetX) + "," + (e + h / 2 + offsetY) + " " + (d - w + offsetX) + "," + (e + offsetY));
        }

        // 对应线的移动
        toMoveLine(nodeObj);
        // 对节点文本处理
        var nodeText = document.getElementById(nodeObj.id + '_text')
        if (nodeText) {
            //			nodeObj = nodeObj.outerHTML;
            nodeText.setAttribute("x", ax + w / 2 + b);
            nodeText.setAttribute("y", ay + h / 2 + c);

            textLineFeed(nodeObj.id, nodeObj.innerHTML);
        }

    }

    /**
     * 点击或选择状态下 样式修改
     * @param {Object} nodeObj对象
     */
    function ChangeObjStyle(nodeObj) {

        if (nodeObj && nodeObj.tagName == "text" && unionNode(null, nodeObj)) {
            nodeObj = unionNode(null, nodeObj);
        }

        if (nodeObj) {
            if (nodeObj.getAttribute("NodeType") == "XNode" || nodeObj.getAttribute("NodeType") == "YNode" || nodeObj.getAttribute("NodeType") == "XNode_text" || nodeObj.getAttribute("NodeType") == "YNode_text") {
                return
            }
        }
        if (nodeObj) {

            var nodeId = nodeObj.id;

            // 如果已经是点击状态样式了，则返回
            if (goalClickStyle.indexOf(nodeId) != -1) {
                return
            }

            if (goalClickStyle != nodeId && nodeObj.tagName != "polyline" && nodeObj.getAttribute("NodeType") != "Pool" && nodeObj.getAttribute("NodeType") != "Area") {
                nodeObj.setAttribute("stroke-width", parseFloat(nodeObj.getAttribute("stroke-width")) + 1);
            } else if (nodeObj.tagName == "polyline") {
                nodeObj.setAttribute("stroke-width", parseFloat(nodeObj.getAttribute("stroke-width")) + 1);
                nodeObj.setAttribute("stroke", "red");
                nodeObj.setAttribute("marker-end", "url(#markerEndArrow2)");
            } else if (nodeObj.getAttribute("NodeType") != "Pool") {
                nodeObj.setAttribute("stroke-width", parseFloat(nodeObj.getAttribute("stroke-width")) + 1);
            }

            // 判断是否按下了Ctrl键
            if (ctrlKey && Tools.isNotBlank(goalClickStyle)) {
                goalClickStyle = goalClickStyle + "," + nodeId;
            } else {
                goalClickStyle = nodeId;
            }
        } else {

            if (goalClickStyle != "") {

                var goalClickStyleArr = goalClickStyle.split(",");
                for (var i = 0; i < goalClickStyleArr.length; i++) {

                    try {
                        var b = evalbroker(goalClickStyleArr[i]);

                        // 解决鼠标左键后没弹起时右键，图形边框会变大问题
                        if (b.tagName == "polyline") {
                            b.setAttribute("stroke", "black");
                            b.setAttribute("stroke-width", "1.5");
                            b.setAttribute("marker-end", "url(#markerEndArrow)");
                        } else {
                            b.setAttribute("stroke-width", "1.5");
                        }
                    } catch (e) {
                    }

                }
                goalClickStyle = ""
            }
        }
    }

    /**
     * 分析汇聚箭头
     */
    function convergeLine() {

        var eleArr = goalClickStyle.split(",");
        var lineArr = new Array();
        for (var i = 0; i < eleArr.length; i++) {
            if (eleArr[i].indexOf("polyline") != -1) {
                lineArr.push(eleArr[i]);
            }
        }

        if (lineArr.length > 1) {
            convergeLineAll(lineArr);

            var p1 = document.getElementById(lineArr[0]).getFirstPoint();
            var lineId = lineArr[0];
            var oo = lineId.replace("polyline", "").split("_");
            var sobj = evalbroker("Node" + oo[0]);
            var pointType = route.getPointType(p1.x, p1.y, sobj);

            // 对边上线进行排序
            route.sortSide(sobj, pointType, "LinkeyStartObj");

        }

        // 缓存汇聚的箭头，用于拆分
        cacheConvergeLine = goalClickStyle;

        // 修改取消选中时的样式
        ChangeObjStyle();

    }

    /**
     * 箭头汇聚
     */
    function convergeLineAll(lineArr) {

        var flag = true; //方向标识  表示true左右方向，false 表示上下
        var cornerL = CONFIG.LINE.cornerL;

        var baseLine = document.getElementById(lineArr[0]);
        var basePoint = baseLine.getLinePointsArr();

        //		debugger;
        //		console.log(basePoint);

        var p1 = basePoint[0];
        var p2 = basePoint[1];

        if (p1.x == p2.x) {
            flag = false;
        }

        for (i = 1; i < lineArr.length; i++) {

            var lineObj = document.getElementById(lineArr[i]);
            var pointsJson = lineObj.getLinePointsArr();

            if (flag && pointsJson[0].y == pointsJson[1].y) {
                pointsJson[0] = p1;
                pointsJson[1].y = p1.y;

                lineObj.setPoints(pointsJson);
            } else if (!flag && pointsJson[0].x == pointsJson[1].x) {
                pointsJson[0] = p1;
                pointsJson[1].x = p1.x;

                lineObj.setPoints(pointsJson);
            }

        }

    }

    /**
     * 汇聚后拆分
     */
    function splitLine() {
        // 1、找目标。按住ctrl拆分选项，上一个缓存汇聚cacheConvergeLine

        if (Tools.isNotBlank(goalClickStyle)) {
            cacheConvergeLine = goalClickStyle;
        }

        // 2、对lineArr进行排序输出

        if (Tools.isNotBlank(cacheConvergeLine)) {

            sortEqual = true;

            var lineArr = cacheConvergeLine.split(",");
            var p1 = document.getElementById(lineArr[0]).getFirstPoint();
            var lineId = lineArr[0];
            var oo = lineId.replace("polyline", "").split("_");
            var sobj = evalbroker("Node" + oo[0]);
            var pointType = route.getPointType(p1.x, p1.y, sobj);

            // 对边上线进行排序
            route.sortSide(sobj, pointType, "LinkeyStartObj");

            cacheConvergeLine = "";
            sortEqual = false;
        }

        // 修改取消选中时的样式
        ChangeObjStyle();
    }

    /**
     * 删除一条线
     * @param {Object} a 线对象
     */
    function deletepolyline(a) {

        var i = 0;
        var b = new Array();
        var c = new Array();
        var d = a.id;
        if (Tools.isNotBlank(a.getAttribute("LinkeyNode"))) {
            var f = a.getAttribute("LinkeyNode").split("_")
        }

        var g = evalbroker("Node" + f[0]);

        if (Tools.isNotBlank(g) && !freeRulingStatus) {
            var h = g.getAttribute("LinkeyStartObj");
            var k = h.split(",");
            var l = "";
            var j = 0;
            for (i = 0; i < k.length; i++) {
                if (k[i] != d) {
                    b[j] = k[i];
                    j++
                }
            }
            for (i = 0; i < b.length; i++) {
                if (i == b.length - 1) l += b[i];
                else l += b[i] + ","
            }
            g.setAttribute("LinkeyStartObj", l);

            var p1 = a.getFirstPoint();
            var pointType = route.getPointType(p1.x, p1.y, g);
            route.sortSide(g, pointType, "LinkeyStartObj");
        }

        try {
            g = evalbroker("Node" + f[1]);

            if (Tools.isNotBlank(g) && !freeRulingStatus) {
                var m = g.getAttribute("LinkeyEndObj");
                k = m.split(",");
                l = "";
                j = 0;
                for (i = 0; i < k.length; i++) {
                    if (k[i] != d) {
                        c[j] = k[i];
                        j++
                    }
                }
                for (i = 0; i < c.length; i++) {
                    if (i == c.length - 1) l += c[i];
                    else l += c[i] + ","
                }
                g.setAttribute("LinkeyEndObj", l)

                var p1 = a.getLastPoint();
                var pointType = route.getPointType(p1.x, p1.y, g);
                route.sortSide(g, pointType, "LinkeyEndObj");
            }
        } catch (e) {
            alert("deletepolyline error 2=" + e.message)
        }

        try {
            var n = eval(a.id + "_text");
            n.parentNode.removeChild(n);
        } catch (e) {
        }
        delPropertyDoc(a)

    }

    /**
     * 将路由id添加到起始节点的LinkeyStartObj 或 LinkeyEndObj
     * @param {Object} polylineId 路由id
     */
    function RegObj(polylineId) {

        if (Tools.isNotBlank(goalStartObj)) {
            var c = goalStartObj.getAttribute("LinkeyStartObj");
            if (c == "") {
                goalStartObj.setAttribute("LinkeyStartObj", polylineId);
            } else {
                goalStartObj.setAttribute("LinkeyStartObj", c + "," + polylineId);
            }
            //console.log(goalStartObj.getAttribute("Nodeid") + ":" + goalStartObj.getAttribute("LinkeyStartObj"));
        }
        if (Tools.isNotBlank(goalEndObj)) {
            var c = goalEndObj.getAttribute("LinkeyEndObj");
            if (c == "") {
                goalEndObj.setAttribute("LinkeyEndObj", polylineId);
            } else {
                goalEndObj.setAttribute("LinkeyEndObj", c + "," + polylineId);
            }
            //console.log(goalStartObj.getAttribute("Nodeid") + ":" + goalStartObj.getAttribute("LinkeyEndObj"));
        }

    }


    /**
     保存图片
     */
    function savePicture() {

        var maxSVGWidth = 0;
        var maxSVGHeight = 0;

        for (var i = 0; i < document.all.length; i++) {
            var domObj = document.all(i);
            var domTagName = domObj.tagName.toLowerCase();

            if ((domTagName == "rect" || domTagName == "path" || domTagName == "circle" || domTagName == "polygon")) {

                var leftx = parseInt(converterSVG(domObj, "left")) - 0 + offsetX;
                var topy = parseInt(converterSVG(domObj, "top")) - 0 + offsetY;

                if (leftx > maxSVGWidth) {
                    maxSVGWidth = leftx;
                }

                if (topy > maxSVGHeight) {
                    maxSVGHeight = topy;
                }
            }

        }

        maxSVGWidth += 150;
        maxSVGHeight += 150;

        console.log("maxSVGWidth: " + maxSVGWidth + "\nmaxSVGHeight: " + maxSVGHeight);

        location.href = "r?wf_num=R_S002_B022&Processid=" + top.processid + "&maxSVGWidth=" + maxSVGWidth + "&maxSVGHeight=" + maxSVGHeight;
    }


    /**
     移除原有样式，解决旧版本不断新增<defs>问题
     */
    function removeDefs() {

        if ($("#Activity").length > 0) {
            $("#Activity").parent().remove();
            removeDefs();
        } else {
            return true;
        }

    }

    /**
     * svg 节点样式描述函数
     */
    function svginit() {

        removeDefs(); //移除重复的样式

        var svgroot = document.getElementById("svg");
        var defs = svg.create("defs").appendTo(svgroot).node;
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "Activity",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#daeef3"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "AutoActivity",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#C0C0C0"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "SubProcess",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#daeef3"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "OutProcess",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#daeef3"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "SNode",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#bebebe"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "Event",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#e8e8e8"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "StartNode",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#0c0"
        }).appendTo(linearGradient);
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient);
        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "EndNode",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#fc3"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)

        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "polyline",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "black"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        var marker = svg.create("marker").attrNS({
            id: "markerEndArrow",
            viewBox: "0 0 33 30",
            "refX": "22",
            "refY": "12.5",
            "markerUnits": "strokeWidth",
            "markerWidth": "12",
            "markerHeight": "30",
            "orient": "auto"
        }).appendTo(defs).node;
        var path = svg.create("path").attrNS({
            d: "M6.3125 4.625 15.3125 12.625 6.3125 20.625 21.3125 12.625 Z",
            "stroke-width": "1",
            "stroke": "black",
            "fill": "black",
            "opacity": "1"
        }).appendTo(marker).node;

        var marker = svg.create("marker").attrNS({
            id: "markerEndArrow2",
            viewBox: "0 0 33 30",
            "refX": "22",
            "refY": "12.5",
            "markerUnits": "strokeWidth",
            "markerWidth": "12",
            "markerHeight": "30",
            "orient": "auto"
        }).appendTo(defs).node;
        var path = svg.create("path").attrNS({
            d: "M6.3125 4.625 15.3125 12.625 6.3125 20.625 21.3125 12.625 Z",
            "stroke-width": "1",
            "stroke": "red",
            "fill": "red",
            "opacity": "1"
        }).appendTo(marker).node;

        // 定义泳道线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "XNode",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#f4f4f4"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)
        var linearGradientJ = svg.create("linearGradient").attrNS({
            id: "XNode_rect",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#ccc"
        }).appendTo(linearGradientJ)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradientJ)

        // 定义线性渐变
        var linearGradient = svg.create("linearGradient").attrNS({
            id: "YNode",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#f4f4f4"
        }).appendTo(linearGradient)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradient)

        var linearGradientJ = svg.create("linearGradient").attrNS({
            id: "YNode_rect",
            spreadMethod: "repeat",
            gradientTransform: "rotate(270)"
        }).appendTo(defs).node;
        svg.create("stop").attrNS({
            offset: "5%",
            "stop-color": "#ccc"
        }).appendTo(linearGradientJ)
        svg.create("stop").attrNS({
            offset: "95%",
            "stop-color": "white"
        }).appendTo(linearGradientJ)
    }


    lkwf = {
        // 初始化
        init: init,

        // 获取线的points
        GetPointsStr: GetPointsStr,

        // 计算线(路由/箭头)的起始位置和结束位置
        getObjXY: getObjXY,

        // 创建线(箭头)
        createLineObj: createLineObj,

        // 计算结束节点小圆圈位置
        calculateEndHotPoint: calculateEndHotPoint,

        // 创建边界箭头
        createBoundaryLine: createBoundaryLine,

        // 创建自由折线
        createFreeLine: createFreeLine,

        // 自由折线取消划线状态
        cancelFreeLine: cancelFreeLine,

        // 节点文本多行时的处理
        textLineFeed: textLineFeed,

        // 修改大小
        editArea: editArea,

        // 移动端线
        toMoveLine: toMoveLine,

        // 线的重绘
        toRedraw: toRedraw,

        // 校验鼠标在节点旁边是否有效
        checkConnect: checkConnect,

        // 显示点击状态
        ChangeObjStyle: ChangeObjStyle,

        // 汇聚箭头
        convergeLine: convergeLine,

        // 移动线段
        MoveLine: MoveLine,

        // 删除一条线
        deletepolyline: deletepolyline,

        // 拆分线
        splitLine: splitLine,

        // 保存图片
        savePicture: savePicture
    }
    return dcanvas;
})