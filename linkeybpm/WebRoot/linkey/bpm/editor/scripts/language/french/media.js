function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Largeur";
    txtLang[2].innerHTML = "Hauteur";   
    txtLang[3].innerHTML = "D\u00E9marrage automatique"; 
    txtLang[4].innerHTML = "Affiche les Contr\u00F4les";
    txtLang[5].innerHTML = "Affiche la barre des Status";   
    txtLang[6].innerHTML = "Affiche La pr\u00E9sentation";
    txtLang[7].innerHTML = "Rembobinage Automatique";       

    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "ins\u00E9rer";
    document.getElementById("btnApply").value = "actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Insertion d\u0027un Media</title>")
    }
