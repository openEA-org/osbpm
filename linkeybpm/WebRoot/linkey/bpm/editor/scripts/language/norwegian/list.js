function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Tall & bokstaver";
    txtLang[1].innerHTML = "Bildepunkt";
    txtLang[2].innerHTML = "Start - nr.";
    txtLang[3].innerHTML = "Venstre marg";
    txtLang[4].innerHTML = "Bruk bilde"
    txtLang[5].innerHTML = "Venstre marg";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";  
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "Velg en type."
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Punktoppstilling</title>")
    }
