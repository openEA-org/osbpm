function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "St\u00F8rrelse";
    txtLang[2].innerHTML = "Tillad flere valg";
    txtLang[3].innerHTML = "V\u00E6rdier";

    document.getElementById("btnAdd").value = "  tilf\u00F8j  ";
    document.getElementById("btnUp").value = "  Op  ";
    document.getElementById("btnDown").value = "  Ned  ";
    document.getElementById("btnDel").value = "  Slet  ";
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Rulleliste</title>")
    }