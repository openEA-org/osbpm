function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Rad";
    txtLang[1].innerHTML = "Celleavstand";
    txtLang[2].innerHTML = "Kolonne";
    txtLang[3].innerHTML = "Cellemargen";
    txtLang[4].innerHTML = "Ramme";
    txtLang[5].innerHTML = "Kollapse";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Ingen";
    optLang[1].text = "Ja";
    optLang[2].text = "Nei";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";

    document.getElementById("btnSpan1").value = "Span v";
    document.getElementById("btnSpan2").value = "Span >";
    }
function writeTitle()
    {
    document.write("<title>"+"Sett inn tabell"+"</title>")
    }