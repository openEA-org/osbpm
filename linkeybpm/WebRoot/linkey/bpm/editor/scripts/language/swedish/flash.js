function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "K\u00E4lla";
    txtLang[1].innerHTML = "Bakgrund";
    txtLang[2].innerHTML = "Bredd";
    txtLang[3].innerHTML = "H\u00F6jd";
    txtLang[4].innerHTML = "Kvalitet";  
    txtLang[5].innerHTML = "L\u00E4ge";  
    txtLang[6].innerHTML = "Repetera";
    txtLang[7].innerHTML = "Ja";
    txtLang[8].innerHTML = "Nej";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "Pluginsida";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "L\u00E5g"
    optLang[1].text = "H\u00F6g"
    optLang[2].text = "<Ej valt>"
    optLang[3].text = "V\u00E4nster"
    optLang[4].text = "H\u00F6ger"
    optLang[5].text = "\u00D6verst"
    optLang[6].text = "Nederst"
    
    document.getElementById("btnPick").value = "V\u00E4lj";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Infoga Flash</title>")
    }