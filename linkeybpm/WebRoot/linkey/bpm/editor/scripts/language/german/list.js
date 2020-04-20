function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nummeriert";
    txtLang[1].innerHTML = "Aufz&auml;lung";
    txtLang[2].innerHTML = "Start Nummer";
    txtLang[3].innerHTML = "Linker Rand";
    txtLang[4].innerHTML = "Bild verwenden - url"
    txtLang[5].innerHTML = "Linker Rand";
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";  
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "Bitte w\u00E4hlen Sie eine Liste aus.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Listenformatierung</title>")
    }
