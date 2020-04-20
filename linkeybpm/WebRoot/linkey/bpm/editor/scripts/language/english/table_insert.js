function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Rows";
    txtLang[1].innerHTML = "Spacing";
    txtLang[2].innerHTML = "Columns";
    txtLang[3].innerHTML = "Padding";
    txtLang[4].innerHTML = "Borders";
    txtLang[5].innerHTML = "Collapse";
    
	var optLang = document.getElementsByName("optLang");
    optLang[0].text = "No Border";
    optLang[1].text = "Yes";
    optLang[2].text = "No";
    
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnInsert").value = "insert";

    document.getElementById("btnSpan1").value = "Span v";
    document.getElementById("btnSpan2").value = "Span >";
    }
function writeTitle()
    {
    document.write("<title>Insert Table</title>")
    }