function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Alignment";
    txtLang[1].innerHTML = "Indentation";
    txtLang[2].innerHTML = "Word Spacing";
    txtLang[3].innerHTML = "Character Spacing";
    txtLang[4].innerHTML = "Line Height";
    txtLang[5].innerHTML = "Text Case";
    txtLang[6].innerHTML = "White Space";
    
    document.getElementById("divPreview").innerHTML = "Lorem ipsum dolor sit amet, " +
        "consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Not Set";
    optLang[1].text = "Left";
    optLang[2].text = "Right";
    optLang[3].text = "Center";
    optLang[4].text = "Justify";
    optLang[5].text = "Not Set";
    optLang[6].text = "Capitalize";
    optLang[7].text = "Uppercase";
    optLang[8].text = "Lowercase";
    optLang[9].text = "None";
    optLang[10].text = "Not Set";
    optLang[11].text = "No Wrap";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnApply").value = "apply";
    document.getElementById("btnOk").value = " ok ";   
    }
function writeTitle()
    {
    document.write("<title>Paragraph Formatting</title>")
    }
