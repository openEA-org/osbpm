function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "K\u00E4lla";
    txtLang[1].innerHTML = "Bokm\u00E4rke";
    txtLang[2].innerHTML = "M\u00E5l";
    txtLang[3].innerHTML = "Rubrik";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Samma f\u00F6nster";
    optLang[1].text = "Nytt f\u00F6nster";
    optLang[2].text = "\u00D6verordnad ram";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>" + "Hyperl\u00E4nk" + "</title>")
    }