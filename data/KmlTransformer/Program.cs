using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace KmlTransformer
{
    class Program
    {
        static void Main(string[] args)
        {

            var files = new int[] { 1, 2, 3, 4, 5, 6 };

            var sb = new StringBuilder();
            sb.AppendLine("INSERT INTO Estaciones (linea, nombre, ubicacion, svg)");
            bool first = true;
            foreach (var file in files)
            {
                sb.AppendLine($"-- Linea {file}");

                XmlDocument doc = new XmlDocument();
                doc.Load($"l{file}.kml");
                var places = ((XmlElement)doc.DocumentElement.GetElementsByTagName("Document")[0]).GetElementsByTagName("Placemark").Cast<XmlNode>();


                foreach (XmlElement place in places.Skip(1))
                {
                    Write(sb, file, place, first);
                    first = false;
                }
            }
            sb.AppendLine(";");
            System.IO.File.WriteAllText($"data.sql", sb.ToString());
        }

        static void Write(StringBuilder sb, int line, XmlElement place, bool isFirst = false)
        {
            var nombreEstacion = place.GetElementsByTagName("name")[0].InnerText;
            var points = place.GetElementsByTagName("Point");
            if (points.Count > 0 && !(nombreEstacion.Contains("punto") || nombreEstacion.Contains("Punto")))
            {
                var coordenadas = points[0].ChildNodes[0].InnerText.Split(',')
                       .Select(s => Double.Parse(s)).ToList();
                if (isFirst)
                    sb.Append("SELECT ");
                else
                    sb.Append("UNION ALL SELECT ");
                sb.AppendLine($"{line},'{nombreEstacion.Replace("Estacion","").Trim().Replace("'", "''")}',MakePoint({coordenadas[0]}, {coordenadas[1]}, 4326), NULL");
            }
        }
    }
}
