$(document).ready(function() {
    var ua = window.navigator.userAgent;
    var ieVerson = getIEVerson();
    if(ieVerson < 8 ) {
        alert('为了得到更好的体验效果，请使用IE8以上浏览器');
        window.location.href = 'about:blank';
        window.opener = null;
        window.open('','_self');
        window.close();
    }
    if (ieVerson != 99) {
        $(".login_logo3").css("visibility", "hidden");
    }
});

function getIEVerson() {
    var userAgent = navigator.userAgent;
    var isOpera = userAgent.indexOf("Opera") > -1;
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera;
    if (isIE) {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        return fIEVersion;
    }
    return 99;
}