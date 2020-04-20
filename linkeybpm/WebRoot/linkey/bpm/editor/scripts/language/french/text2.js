var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "S\u00E9lection d\u0027une Police par ordre de pr\u00E9f\u00E9rence";
    txtLang[1].innerHTML = "Style";
    txtLang[2].innerHTML = "Taille";
    txtLang[3].innerHTML = "Couleur du Texte";
    txtLang[4].innerHTML = "Couleur de l\u0027arri\u00E8re plan";
    
    txtLang[5].innerHTML = "D\u00E9coration";
    txtLang[6].innerHTML = "Casse";
    txtLang[7].innerHTML = "Petites Majuscules";
    txtLang[8].innerHTML = "Alignement Vertical";

    txtLang[9].innerHTML = "Non d\u00E9fini";
    txtLang[10].innerHTML = "Soulign\u00E9";
    txtLang[11].innerHTML = "Surlign\u00E9";
    txtLang[12].innerHTML = "Barr\u00E9";
    txtLang[13].innerHTML = "Aucun";

    txtLang[14].innerHTML = "Non d\u00E9fini";
    txtLang[15].innerHTML = "Nom propre";
    txtLang[16].innerHTML = "Majuscule";
    txtLang[17].innerHTML = "Minuscule";
    txtLang[18].innerHTML = "Aucun";

    txtLang[19].innerHTML = "Non d\u00E9fini";
    txtLang[20].innerHTML = "Petite majuscule";
    txtLang[21].innerHTML = "Normal";

    txtLang[22].innerHTML = "Non d\u00E9fini";
    txtLang[23].innerHTML = "Exposant";
    txtLang[24].innerHTML = "Indice";
    txtLang[25].innerHTML = "Relatif";
    txtLang[26].innerHTML = "Ligne de base";
    
    txtLang[27].innerHTML = "Espacement des caract\u00E8res";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Normal"
    optLang[1].text = "Italique"
    optLang[2].text = "Gras"
    optLang[3].text = "Gras/Italique"
    
    optLang[0].value = "Normal"
    optLang[1].value = "Italique"
    optLang[2].value = "Gras"
    optLang[3].value = "Gras/Italique"
    
    sStyleWeight1 = "Normal"
    sStyleWeight2 = "Italique"
    sStyleWeight3 = "Gras"
    sStyleWeight4 = "Gras/Italique"
    
    optLang[4].text = "Haut"
    optLang[5].text = "Milieu"
    optLang[6].text = "Bas"
    optLang[7].text = "D\u00E9but Texte"
    optLang[8].text = "Fin Texte"
    
    document.getElementById("btnPick1").value = "Choisir";
    document.getElementById("btnPick2").value = "Choisir";

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
    document.write("<title>D\u00E9finition d\u0027un Style</title>")
    }