function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nom";
    txtLang[1].innerHTML = "Dimension";
    txtLang[2].innerHTML = "S\u00E9lection multiple";
    txtLang[3].innerHTML = "Valeurs";
    
    document.getElementById("btnAdd").value = "ajouter";
    document.getElementById("btnUp").value = "monter";
    document.getElementById("btnDown").value = "descendre";
    document.getElementById("btnDel").value = "supprimer";
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Liste</title>")
    }