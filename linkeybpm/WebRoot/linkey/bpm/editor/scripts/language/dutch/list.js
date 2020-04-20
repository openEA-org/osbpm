function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Numbered";
    txtLang[1].innerHTML = "Bulleted";
    txtLang[2].innerHTML = "Starting Number";
    txtLang[3].innerHTML = "Left Margin";
    txtLang[4].innerHTML = "Using Image - url"
    txtLang[5].innerHTML = "Left Margin";
    
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnApply").value = "apply";
    document.getElementById("btnOk").value = " ok ";   
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "Please select a list.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>List Formatting</title>")
    }
