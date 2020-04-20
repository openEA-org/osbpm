function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Arri\u00E8re plan";
    txtLang[2].innerHTML = "Largeur";
    txtLang[3].innerHTML = "Hauteur";
    txtLang[4].innerHTML = "Qualit\u00E9e";
    txtLang[5].innerHTML = "Aligner";
    txtLang[6].innerHTML = "Boucle";
    txtLang[7].innerHTML = "Oui";
    txtLang[8].innerHTML = "Non";
    
    txtLang[9].innerHTML = "ID de la Classe";
    txtLang[10].innerHTML = "Code de Base";
    txtLang[11].innerHTML = "Page Plugins";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Bas"
    optLang[1].text = "Haut"
    optLang[2].text = "<Aucun>"
    optLang[3].text = "Gauche"
    optLang[4].text = "Droite"
    optLang[5].text = "Dessus"
    optLang[6].text = "Dessous"
    
    document.getElementById("btnPick").value = "Choisir";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Insertion d\u0027une animation Flash&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }