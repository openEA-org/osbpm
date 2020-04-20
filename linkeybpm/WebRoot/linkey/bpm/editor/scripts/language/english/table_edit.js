function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    //txtLang[0].innerHTML = "Size";
    txtLang[0].innerHTML = "AutoFit";
    txtLang[1].innerHTML = "Properties";
    txtLang[2].innerHTML = "Style";
    //txtLang[4].innerHTML = "Insert Row";
    //txtLang[5].innerHTML = "Insert Column";
    //txtLang[6].innerHTML = "Span/Split Row";
    //txtLang[7].innerHTML = "Span/Split Column";
    //txtLang[8].innerHTML = "Delete Row";
    //txtLang[9].innerHTML = "Delete Column";    
    txtLang[3].innerHTML = "Width";
    txtLang[4].innerHTML = "AutoFit to contents";
    txtLang[5].innerHTML = "Fixed table width";
    txtLang[6].innerHTML = "AutoFit to window";
    txtLang[7].innerHTML = "Height";
    txtLang[8].innerHTML = "AutoFit to contents";
    txtLang[9].innerHTML = "Fixed table height";
    txtLang[10].innerHTML = "AutoFit to window";
    txtLang[11].innerHTML = "Alignment";
    txtLang[12].innerHTML = "Margin";
    txtLang[13].innerHTML = "Left";
    txtLang[14].innerHTML = "Right";
    txtLang[15].innerHTML = "Top";
    txtLang[16].innerHTML = "Bottom";
    txtLang[17].innerHTML = "Borders";
    txtLang[18].innerHTML = "Collapse";
    txtLang[19].innerHTML = "Background";
    txtLang[20].innerHTML = "Cell Spacing";
    txtLang[21].innerHTML = "Cell Padding";
    txtLang[22].innerHTML = "CSS Text";
        
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "percent"
    optLang[2].text = "pixels"
    optLang[3].text = "percent"
    optLang[4].text = "left"
    optLang[5].text = "center"
    optLang[6].text = "right"
    optLang[7].text = "No Border"
    optLang[8].text = "Yes"
    optLang[9].text = "No"

    document.getElementById("btnPick").value="Pick";
    document.getElementById("btnImage").value="Image";

    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnApply").value = "apply";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Cannot delete column. The column contains spanned cells from another column. Please remove the span first.";
        case "Cannot delete row.":
            return "Cannot delete row. The row contains spanned cells from another rows. Please remove the span first.";
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Table Properties</title>")
    }