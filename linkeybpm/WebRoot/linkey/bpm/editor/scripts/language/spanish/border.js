function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Color";
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnOk").value = " Aplicar ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Sin Bordes";
        case "Outside Border": return "Borde alrededor";
        case "Left Border": return "Borde izquierdo";
        case "Top Border": return "Borde encima";
        case "Right Border": return "Borde derecho";
        case "Bottom Border": return "Borde abajo";
        case "Pick": return "Seleccionar";
        case "Custom Colors": return "Colores personalizados";
        case "More Colors...": return "Mas Colores...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Bordes</title>")
    }