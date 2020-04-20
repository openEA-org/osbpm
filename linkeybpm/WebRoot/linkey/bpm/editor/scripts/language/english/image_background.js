function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Image Source";
    txtLang[1].innerHTML = "Repeat";
    txtLang[2].innerHTML = "Horizontal Align";
    txtLang[3].innerHTML = "Vertical Align";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Repeat"
    optLang[1].text = "No repeat"
    optLang[2].text = "Repeat horizontally"
    optLang[3].text = "Repeat vertically"
    optLang[4].text = "left"
    optLang[5].text = "center"
    optLang[6].text = "right"
    optLang[7].text = "pixels"
    optLang[8].text = "percent"
    optLang[9].text = "top"
    optLang[10].text = "center"
    optLang[11].text = "bottom"
    optLang[12].text = "pixels"
    optLang[13].text = "percent"
    
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Background Image</title>")
    }

