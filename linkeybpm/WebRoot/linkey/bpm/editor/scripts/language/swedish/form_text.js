function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Typ";
    txtLang[1].innerHTML = "Namn";
    txtLang[2].innerHTML = "Bredd";
    txtLang[3].innerHTML = "Maxl\u00E4ngd";
    txtLang[4].innerHTML = "Antal rader";
    txtLang[5].innerHTML = "V\u00E4rde";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Text"
    optLang[1].text = "Textarea"
    optLang[2].text = "L\u00F6senord"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>" + "Textf\u00E4lt" + "</title>")
    }