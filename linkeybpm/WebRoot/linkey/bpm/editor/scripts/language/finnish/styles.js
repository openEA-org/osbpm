function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Format";
    txtLang[1].innerHTML = "F\u00F6rhandsgranska";
    txtLang[2].innerHTML = "Applicera p\u00E5";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Vald text"
    optLang[1].text = "Aktuell tagg"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "Du har valt BODY taggen.";
        case "Please select a text.":
            return "V\u00E4nligen v\u00E4lj text.";
        default:return "";
        }
    }    
function writeTitle()
    {
    document.write("<title>Formatmallar</title>")
    }
