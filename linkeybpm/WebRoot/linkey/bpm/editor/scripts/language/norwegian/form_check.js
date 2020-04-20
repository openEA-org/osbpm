function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "Verdi";
    txtLang[2].innerHTML = "Starttilstand";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Markert"
    optLang[1].text = "Ikke markert"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Tilf\u00F8y";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Avkryssningsfelt</title>")
    }