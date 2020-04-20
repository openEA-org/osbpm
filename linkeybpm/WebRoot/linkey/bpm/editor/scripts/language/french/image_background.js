function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source de l\u0027image";
    txtLang[1].innerHTML = "R\u00E9p\u00E9tition";
    txtLang[2].innerHTML = " Align. Horizontal";
    txtLang[3].innerHTML = "Align.Vertical";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "R\u00E9p\u00E9ter";
    optLang[1].text = "Ne pas r\u00E9p\u00E9ter";
    optLang[2].text = "R\u00E9p\u00E9tition horizontale";
    optLang[3].text = "R\u00E9p\u00E9tition verticale";
    optLang[4].text = "Gauche";
    optLang[5].text = "Centre";
    optLang[6].text = "Droit";
    optLang[7].text = "pixels";
    optLang[8].text = "pourcentage";
    optLang[9].text = "Haut";
    optLang[10].text = "Milieu";
    optLang[11].text = "Bas";
    optLang[12].text = "pixels";
    optLang[13].text = "pourcentage";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Image d\u0027arri\u00E8re plan</title>")
    }

