function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "V\u00E6rdi";

    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Skjult felt</title>")
    }