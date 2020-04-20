function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Styles";
    txtLang[1].innerHTML = "Preview";
    txtLang[2].innerHTML = "Apply to";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Selected Text"
    optLang[1].text = "Current Tag"
    
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnApply").value = "apply";
    document.getElementById("btnOk").value = " ok ";
    }
    
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "You're selecting BODY element.";
        case "Please select a text.":
            return "Please select a text.";
        default:return "";
        }
    }
    
function writeTitle()
    {
    document.write("<title>Styles</title>")
    }
