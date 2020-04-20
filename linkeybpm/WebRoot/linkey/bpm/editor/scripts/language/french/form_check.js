function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nom";
    txtLang[1].innerHTML = "Valeur";
    txtLang[2].innerHTML = "D\u00E9faut";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Coch\u00E9";
    optLang[1].text = "Non coch\u00E9";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Case \u00E0 Cocher</title>")
    }