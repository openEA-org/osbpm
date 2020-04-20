function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Tal & bogstaver";
    txtLang[1].innerHTML = "Billedepunkt";
    txtLang[2].innerHTML = "Start - nr.";
    txtLang[3].innerHTML = "Venstre margen";
    txtLang[4].innerHTML = "Brug billede"
    txtLang[5].innerHTML = "Venstre margen";
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";    
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "V\u00E6lg en type."
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Punktopstilling</title>")
    }
