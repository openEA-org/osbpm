function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Background";
    txtLang[2].innerHTML = "Width";
    txtLang[3].innerHTML = "Height";
    txtLang[4].innerHTML = "Quality";
    txtLang[5].innerHTML = "Align";
    txtLang[6].innerHTML = "Loop";
    txtLang[7].innerHTML = "Yes";
    txtLang[8].innerHTML = "No";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "PluginsPage";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Low"
    optLang[1].text = "High"
    optLang[2].text = "<Not Set>"
    optLang[3].text = "Left"
    optLang[4].text = "Right"
    optLang[5].text = "Top"
    optLang[6].text = "Bottom"
    
    document.getElementById("btnPick").value = "Kiezen";
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnOk").value = " ok ";
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
    document.write("<title>Insert Flash</title>")
    }