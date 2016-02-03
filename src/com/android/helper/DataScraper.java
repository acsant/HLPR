package com.android.helper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class DataScraper extends AsyncTask<String, Integer, Integer> {

	private static ArrayList<VolunteerData> volunteerData = new ArrayList<VolunteerData>();
	public InformationDisplay.HelperIntro caller;
	private static int dataCount = 0;
	public ProgressDialog progressDialog = null;
	private static final int MAX_PROGRESS = 99;
	private static String city = "";
	private static String province = "";
	private static String keyword = "";

	public DataScraper(InformationDisplay.HelperIntro caller) {
		this.caller = caller;
	}

	public static Document getDocumentFromUrl (String url) throws IOException {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (HttpStatusException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public Element getElementsWithTag (String tag, Document doc) {
		Element elementByTag = doc.getElementById(tag);
		return elementByTag;
	}

	public static Elements getValidVolunteerData (Document doc) {
		if (doc != null) {
			Elements links = doc.getElementsByTag("a");
			Elements dataElements = new Elements();
			for (Element link : links) {
				if (link.attr("title").contains("Volunteer information")) {
					dataElements.add(link);
				}
			}
			return dataElements;
		}
		return new Elements();
	}

	public void createDataObjects (Elements dataElements) {
		for (Element dataElement : dataElements) {
			Document elementDoc = null;
			try {
				elementDoc = getDocumentFromUrl(dataElement.attr("abs:href"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			String docName = elementDoc.title().toString().substring(0, elementDoc.title().toString().indexOf(":"));
			String[] contactInfo = getElementContact(elementDoc);
			// Please dont remove this comment for future reference - dataElement.attr("abs:href")
			volunteerData.add(new VolunteerData(contactInfo[2], contactInfo[0], getElementCategory(elementDoc), docName, contactInfo[1]));
			dataCount ++;
			double progress = (dataCount*100) / dataElements.size();
			if ((int) progress <= MAX_PROGRESS && (int) progress != 100) {
				publishProgress((int) progress);
			}
		}
	}

	public static String getPhoneNumber(Document doc) {
		String docName = doc.title().toString().substring(0, doc.title().toString().indexOf(":"));
		Elements phoneElements = doc.select("img[ALT$=" + docName + " Phone Number]");
		String containsPhone = "";
		String phoneData = "";
		if (phoneElements.size() > 0) {
			containsPhone = phoneElements.first().toString();
		}
		if (!containsPhone.isEmpty()) {
			phoneData = containsPhone.substring(containsPhone.lastIndexOf("Phone Number"), containsPhone.indexOf("</p>"));
		}
		return phoneData;
	}

	public static String getElementCategory(Document doc) {
		Elements categoryElements = doc.select("table[width$=640]");
		String containsKeyword = categoryElements.first().toString();
		String category = containsKeyword.substring(containsKeyword.lastIndexOf("Keywords"), containsKeyword.lastIndexOf("</td>"));
		return category;
	}

	public static String[] getElementContact (Document doc) {
		String[] results = new String[3];
		ArrayList<Element> elemList = new ArrayList<Element>();
		Elements addressElements = doc.getElementsByTag("tr");
		Element dataElement = null;
		Element phoneElement = null;
		Element urlElement = null;
		String dataToManipulate = "";
		String docName = doc.title().toString().substring(0, doc.title().toString().indexOf(":"));
		if (docName.contains("&")) {
			docName = docName.replace("&", "&amp;");
		}
		for (Element addElem : addressElements) {
			elemList.add(addElem);
		}
		if (!elemList.get(8).toString().contains("Branch")) {
			dataElement = elemList.get(8);
			phoneElement = elemList.get(8);
		} else {
			for (int i = 8; i < elemList.size(); i++) {
				if (elemList.get(i).toString().contains(city)) {
					dataElement = elemList.get(i);
					phoneElement = elemList.get(i);
					urlElement = elemList.get(i);
					break;
				}
			}
		}

		String dataString = dataElement.toString();
		if (dataString.contains("Branch") && !city.isEmpty()) {
			if (dataString.contains("Branch")) {
				dataToManipulate = dataElement.toString().substring(dataElement.toString().indexOf("Branch"));
				if (dataToManipulate.contains(city)) {
					dataToManipulate = dataToManipulate.substring(dataToManipulate.indexOf("<br />") + 6);
				} 
			}
		} else if (dataString.contains("<b>" + docName + "<br />")) {
			dataToManipulate = dataElement.toString().substring(dataElement.toString().indexOf("<b>" + docName + "<br />"));
			dataToManipulate = dataToManipulate.substring(dataToManipulate.indexOf("<br />") + 6);
			dataToManipulate = dataToManipulate.substring(dataToManipulate.indexOf("<br />") + 6);
		} else {
			dataToManipulate = dataElement.toString().substring(dataElement.toString().indexOf("<br />") + 6);
		}
		if (!dataToManipulate.isEmpty()) {
			String address = dataToManipulate;
			address = address.substring(0, address.indexOf("<br />"));
			results[0] = address;

			//Extract Phone information
			String phone = "";
			Element compareElem = doc.select("img[ALT$=" + docName + " Phone Number]").first();
			if (compareElem != null && phoneElement.toString().contains(compareElem.toString())) {
				phone = phoneElement.toString().substring(phoneElement.toString().indexOf("Phone Number"));
				phone = phone.substring(phone.indexOf(">") + 1, phone.indexOf("<"));
			}
			results[1] = phone;
			
			//Extract link
			String url = "";
			Element urlElem = doc.select("a[title$=" + docName + " website").first();
			if (urlElem != null) {
				url = urlElem.attr("abs:href");
			}
			results[2] = url;
		}
		return results;
	}

	public static Elements filterDataElements(Elements dataElements){
		Elements filter = new Elements();
		for (Element dataElement : dataElements) {
			if (!dataElement.attr("abs:href").equals("http://www.c`anadian-universities.net/Volunteer/Centre-daction-benevole-et-communautaire-Saint-Laurent.html") &&
					!dataElement.attr("abs:href").equals("http://www.canadian-universities.net/Volunteer/The-Duke-of-Edinburghs-Award.html")) {
				filter.add(dataElement);
			}
		}
		return filter;
	}

	public static ArrayList<VolunteerData> getVolunteerData() {
		return volunteerData;
	}

	public static void setVolunteerData(ArrayList<VolunteerData> volunteerData) {
		DataScraper.volunteerData = volunteerData;
	}

	@Override
	public void onPreExecute () {
		super.onPreExecute();
		progressDialog = new ProgressDialog(caller.getActivity());
		progressDialog.setTitle("Please Wait");
		progressDialog.setMessage("Searching...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(MAX_PROGRESS);
		progressDialog.setProgressNumberFormat("");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	@Override
	protected Integer doInBackground(String... params) {
		volunteerData = new ArrayList<VolunteerData>();
		province = params[0];
		city = params[1];
		keyword = params[2];
		dataCount = 0;

		final String DOT_HTML = ".html";
		String sourceLink = "http://www.canadian-universities.net/Volunteer/";
		String pages = "";
		Document doc = null;
		Elements pageElems = new Elements();
		// replace " " with "_" so that the link can be created
		if (province.contains(" ")) {
			province = province.replace(" ", "_");
		}
		if (city.contains(" ")) {
			city = city.replace(" ", "_");
		}
		if (keyword.contains(" ")) {
			keyword = keyword.replace(" ", "_");
		}
		if (keyword.isEmpty()) {
			if (!province.isEmpty() && !city.isEmpty()) {
				sourceLink += province + "-" + city;
			} else {
				sourceLink = null;
			}
		} else {
			if (!province.isEmpty() && !city.isEmpty()) {
				sourceLink += keyword + "-" + province + "-" + city;
			} else {
				sourceLink = null;
			}
		}
		if (sourceLink != null) {
			try {
				doc = getDocumentFromUrl(sourceLink + DOT_HTML);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				pageElems = doc.select("b[CLASS$=navigb]");
			} catch (Exception e) {
				System.out.print("");
			}
			int totalPages = 1;
			if (pageElems.size() > 0) {
				pages = pageElems.last().text().substring(1, pageElems.last().text().length() - 1);
				totalPages = Integer.parseInt(pages);
			}
			for (int i = 1; i <= totalPages; i++) {
				if (i != 1) {
					try {
						doc = getDocumentFromUrl(sourceLink + i + DOT_HTML);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
				Elements dataElements = getValidVolunteerData(doc);
				try {
					createDataObjects(dataElements);
				} catch (Exception e) {
					e.getMessage();
				}
			}
		} 
		int size = volunteerData.size();
		System.out.println(size);
		return size;
	}

	@Override
	protected void onProgressUpdate (Integer...progress) {
		progressDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute (Integer i) {
		caller.addThreadResults(volunteerData);
		progressDialog.dismiss();
	}

}
