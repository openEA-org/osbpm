function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Numrerad lista";
    txtLang[1].innerHTML = "Punktlista";
    txtLang[2].innerHTML = "B\u00F6rja med tecken";
    txtLang[3].innerHTML = "V\u00E4nster marginal";
    txtLang[4].innerHTML = "Anv\u00E4nd bild - k\u00E4lla";
    txtLang[5].innerHTML = "V\u00E4nster marginal";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";  
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "V\u00E4nligen v\u00E4lj en lista.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Punkter och numrering</title>")
    }
