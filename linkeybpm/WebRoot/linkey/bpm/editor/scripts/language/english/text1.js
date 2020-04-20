var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Font";
    txtLang[1].innerHTML = "Style";
    txtLang[2].innerHTML = "Size";
    txtLang[3].innerHTML = "Foreground";
    txtLang[4].innerHTML = "Background";
    
    txtLang[5].innerHTML = "Decoration";
    txtLang[6].innerHTML = "Text Case";
    txtLang[7].innerHTML = "Minicaps";
    txtLang[8].innerHTML = "Vertical";

    txtLang[9].innerHTML = "Not Set";
    txtLang[10].innerHTML = "Underline";
    txtLang[11].innerHTML = "Overline";
    txtLang[12].innerHTML = "Line-through";
    txtLang[13].innerHTML = "None";

    txtLang[14].innerHTML = "Not Set";
    txtLang[15].innerHTML = "Capitalize";
    txtLang[16].innerHTML = "Uppercase";
    txtLang[17].innerHTML = "Lowercase";
    txtLang[18].innerHTML = "None";

    txtLang[19].innerHTML = "Not Set";
    txtLang[20].innerHTML = "Small-Caps";
    txtLang[21].innerHTML = "Normal";

    txtLang[22].innerHTML = "Not Set";
    txtLang[23].innerHTML = "Superscript";
    txtLang[24].innerHTML = "Subscript";
    txtLang[25].innerHTML = "Relative";
    txtLang[26].innerHTML = "Baseline";
    
    txtLang[27].innerHTML = "Character Spacing";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Regular"
    optLang[1].text = "Italic"
    optLang[2].text = "Bold"
    optLang[3].text = "Bold Italic"
    
    optLang[0].value = "Regular"
    optLang[1].value = "Italic"
    optLang[2].value = "Bold"
    optLang[3].value = "Bold Italic"
    
    sStyleWeight1 = "Regular"
    sStyleWeight2 = "Italic"
    sStyleWeight3 = "Bold"
    sStyleWeight4 = "Bold Italic"
    
    optLang[4].text = "Top"
    optLang[5].text = "Middle"
    optLang[6].text = "Bottom"
    optLang[7].text = "Text-Top"
    optLang[8].text = "Text-Bottom"
    
    document.getElementById("btnPick1").value = "Pick";
    document.getElementById("btnPick2").value = "Pick";

    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnApply").value = "apply";
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
    document.write("<title>Text Formatting</title>")
    }