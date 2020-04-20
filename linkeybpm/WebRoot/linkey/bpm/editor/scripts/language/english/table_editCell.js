function loadTxt()
    {
    
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "AutoFit";
    txtLang[1].innerHTML = "Properties";
    txtLang[2].innerHTML = "Style";
    txtLang[3].innerHTML = "Width";
    txtLang[4].innerHTML = "AutoFit to contents";
    txtLang[5].innerHTML = "Fixed cell width";
    txtLang[6].innerHTML = "Height";
    txtLang[7].innerHTML = "AutoFit to contents";
    txtLang[8].innerHTML = "Fixed cell height";
    txtLang[9].innerHTML = "Text Alignment";
    txtLang[10].innerHTML = "Padding";
    txtLang[11].innerHTML = "Left";
    txtLang[12].innerHTML = "Right";
    txtLang[13].innerHTML = "Top";
    txtLang[14].innerHTML = "Bottom";
    txtLang[15].innerHTML = "White Space";
    txtLang[16].innerHTML = "Background";
    txtLang[17].innerHTML = "Preview";
    txtLang[18].innerHTML = "CSS Text";
    txtLang[19].innerHTML = "Apply to";

    document.getElementById("btnPick").value = "Pick";
    document.getElementById("btnImage").value = "Image";
    document.getElementById("btnText").value = " Text Formatting ";
    document.getElementById("btnBorder").value = " Border Style ";

    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnApply").value = "apply";
    document.getElementById("btnOk").value = " ok ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "percent"
    optLang[2].text = "pixels"
    optLang[3].text = "percent"
    optLang[4].text = "not set"
    optLang[5].text = "top"
    optLang[6].text = "middle"
    optLang[7].text = "bottom"
    optLang[8].text = "baseline"
    optLang[9].text = "sub"
    optLang[10].text = "super"
    optLang[11].text = "text-top"
    optLang[12].text = "text-bottom"
    optLang[13].text = "not set"
    optLang[14].text = "left"
    optLang[15].text = "center"
    optLang[16].text = "right"
    optLang[17].text = "justify"
    optLang[18].text = "Not Set"
    optLang[19].text = "No Wrap"
    optLang[20].text = "pre"
    optLang[21].text = "Normal"
    optLang[22].text = "Current Cell"
    optLang[23].text = "Current Row"
    optLang[24].text = "Current Column"
    optLang[25].text = "Whole Table"
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
    document.write("<title>Cell Properties</title>")
    }