package at.lmk.webapp.components.charts;

public class ScriptTemplates {
	public static final String CHART_AREA = "Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,\"Segoe UI\",Roboto,\"Helvetica Neue\",Arial,sans-serif';\r\n"
			+ "Chart.defaults.global.defaultFontColor = '#292b2c';\r\n" + "\r\n"
			+ "var ctx = document.getElementById(\"[ID]\");\r\n" + "var myLineChart = new Chart(ctx, {\r\n"
			+ "  type: 'line',\r\n" + "  data: {\r\n" + "    labels: [[LABELS]],\r\n" + "    datasets: [{\r\n"
			+ "      label: \"Sessions\",\r\n" + "      lineTension: 0.3,\r\n"
			+ "      backgroundColor: \"rgba(2,117,216,0.2)\",\r\n" + "      borderColor: \"rgba(2,117,216,1)\",\r\n"
			+ "      pointRadius: 5,\r\n" + "      pointBackgroundColor: \"rgba(2,117,216,1)\",\r\n"
			+ "      pointBorderColor: \"rgba(255,255,255,0.8)\",\r\n" + "      pointHoverRadius: 5,\r\n"
			+ "      pointHoverBackgroundColor: \"rgba(2,117,216,1)\",\r\n" + "      pointHitRadius: 50,\r\n"
			+ "      pointBorderWidth: 2,\r\n" + "      data: [[VALUES]],\r\n" + "    }],\r\n" + "  },\r\n"
			+ "  options: {\r\n" + "    scales: {\r\n" + "      xAxes: [{\r\n" + "        time: {\r\n"
			+ "          unit: 'date'\r\n" + "        },\r\n" + "        gridLines: {\r\n"
			+ "          display: false\r\n" + "        },\r\n" + "        ticks: {\r\n"
			+ "          maxTicksLimit: 7\r\n" + "        }\r\n" + "      }],\r\n" + "      yAxes: [{\r\n"
			+ "        ticks: {\r\n" + "          min: [MIN],\r\n" + "          max: [MAX],\r\n"
			+ "          maxTicksLimit: 5\r\n" + "        },\r\n" + "        gridLines: {\r\n"
			+ "          color: \"rgba(0, 0, 0, .125)\",\r\n" + "        }\r\n" + "      }],\r\n" + "    },\r\n"
			+ "    legend: {\r\n" + "      display: false\r\n" + "    }\r\n" + "  }\r\n" + "});\r\n";

	public static final String CHART_PIE = "Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,\"Segoe UI\",Roboto,\"Helvetica Neue\",Arial,sans-serif';\r\n"
			+ "Chart.defaults.global.defaultFontColor = '#292b2c';\r\n" + "\r\n" + "// Pie Chart Example\r\n"
			+ "var ctx = document.getElementById(\"[ID]\");\r\n" + "var myPieChart = new Chart(ctx, {\r\n"
			+ "  type: 'pie',\r\n" + "  data: {\r\n" + "    labels: [[LABELS]],\r\n" + "    datasets: [{\r\n"
			+ "      data: [[VALUES]],\r\n" + "      backgroundColor: ['#007bff', '#dc3545', '#ffc107', '#28a745'],\r\n"
			+ "    }],\r\n" + "  },\r\n" + "});";
}
