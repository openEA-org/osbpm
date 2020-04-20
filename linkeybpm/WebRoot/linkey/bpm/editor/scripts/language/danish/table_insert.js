function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "R\u00E6kke";
    txtLang[1].innerHTML = "Celleafstand";
    txtLang[2].innerHTML = "Kolonne";
    txtLang[3].innerHTML = "Cellemargen";
    txtLang[4].innerHTML = "Ramme";
    txtLang[5].innerHTML = "Collapse";
    
	var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Ingen";
    optLang[1].text = "Ja";
    optLang[2].text = "Nej";
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";

    document.getElementById("btnSpan1").value = "Span v";
    document.getElementById("btnSpan2").value = "Span >";
    }
function writeTitle()
    {
    document.write("<title>"+"Inds\u00E6t tabel"+"</title>")
    }