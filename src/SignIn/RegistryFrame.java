/**@author Mario \n @since (datum) */
package SignIn;

import database.DBConnect;

import javax.swing.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class RegistryFrame extends CompleteFrame {

    //default creating method for the frame to registrate a new servant
    public void registryElems(JTable kellner) {

        this.setTitle("Registrierung");
        this.setSize(400, 350);
        this.setResizable(false);
        this.setLocation(700,300);
        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new SpringLayout());
        //firstname
        JLabel text_vname = new JLabel("Vorname:");
        panel.add(text_vname);
        JTextField field_vname = new JTextField(20);
        text_vname.setLabelFor(field_vname);
        panel.add(field_vname);

        //secondname
        JLabel text_fname = new JLabel("Nachname:");
        panel.add(text_fname);
        JTextField field_fname = new JTextField(20);
        text_fname.setLabelFor(field_fname);
        panel.add(field_fname);

        //social security number
        JLabel text_SVNr = new JLabel("SozialversicherungsNr.:");
        panel.add(text_SVNr);
        JTextField field_SVNr = new JTextField(10);
        text_SVNr.setLabelFor(field_SVNr);
        panel.add(field_SVNr);

        //PLZ
        JLabel text_PLZ = new JLabel("PLZ:");
        panel.add(text_PLZ);
        JTextField field_PLZ = new JTextField(6);
        text_PLZ.setLabelFor(field_PLZ);
        panel.add(field_PLZ);

        //city
        JLabel text_city = new JLabel("Stadt:");
        panel.add(text_city);
        JTextField field_city = new JTextField(20);
        text_city.setLabelFor(field_city);
        panel.add(field_city);

        //create password fields and texts
        JLabel pw = new JLabel("Passwort", JLabel.LEFT);
        panel.add(pw);
        JPasswordField field_pw = new JPasswordField(10);
        pw.setLabelFor(field_pw);
        panel.add(field_pw);
        JLabel pw2 = new JLabel("Passwort bestätigen", JLabel.LEFT);
        panel.add(pw2);
        JPasswordField field_pw2 = new JPasswordField(10);
        pw.setLabelFor(field_pw2);
        panel.add(field_pw2);

        SpringUtilities.makeGrid(panel, 7, 2, 6, 30, 6, 15);

        JButton confirm = new JButton("Registrieren");

        this.add(confirm, BorderLayout.PAGE_END);
        this.add(panel, BorderLayout.NORTH);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        registryConfirm(confirm, field_pw, field_pw2, field_vname, field_fname, field_SVNr, field_PLZ, field_city, this, kellner);
        this.setVisible(true);

    }

    //inserting into the table (database table servant)
    public static void registryConfirm(JButton confirm, JPasswordField pw_field, JPasswordField pw2_field,
                                       JTextField vname_field, JTextField fname_field, JTextField svnr_field,
                                       JTextField plz_field, JTextField city_field, JFrame registry, JTable kellner) {

        DBConnect connect = new DBConnect();
        connect.DBConnect();
        confirm.addActionListener((ActionEvent action) -> {

            String vname = vname_field.getText();
            String fname = fname_field.getText();
            String svnr = svnr_field.getText();
            String plz = plz_field.getText();
            String city = city_field.getText();

            if (Arrays.equals(pw_field.getPassword(), pw2_field.getPassword())) {
                if (isWord(vname) && isWord(fname)) {

                    if (isSVNR(svnr) && isNumber(svnr)) {

                        if (isAdress(plz, city)) {
                            String pw = passwordString(pw_field);

                            if (connect.insert_k(svnr, vname, fname, pw, toInt(plz), city)) {
                                JOptionPane.showConfirmDialog(null, "Erflogreich registriert!", "Succsess", JOptionPane.OK_CANCEL_OPTION);
                                registry.setVisible(false);
                                CompleteFrame.del.setVisible(false);
                                kellner.setModel(CompleteFrame.createJTable_k());
                            } else {

                                JOptionPane.showConfirmDialog(null, "SVNr existiert schon!", "Succsess", JOptionPane.OK_CANCEL_OPTION);
                            }
                        } else {
                            AddressFrame adressFrame = new AddressFrame();
                            adressFrame.createAddressFrame();
                        }
                    } else {
                        JOptionPane.showConfirmDialog(null, "Keine gültige Sozialversicherungsnummer", "Error", JOptionPane.OK_CANCEL_OPTION);
                    }
                } else {
                    JOptionPane.showConfirmDialog(null, "Vorname oder Nachname ist kein gültiger Name", "Error", JOptionPane.OK_CANCEL_OPTION);
                }
            } else if (pw_field.getPassword().toString().isEmpty() || pw2_field.getPassword().toString().isEmpty()){
                JOptionPane.showConfirmDialog(null, "Passwort-Feld ist leer", "Error", JOptionPane.OK_CANCEL_OPTION);
            } else {
                JOptionPane.showConfirmDialog(null, "Passwörter stimmen nicht überein", "Error", JOptionPane.OK_CANCEL_OPTION);
            }
        });
    }

    //password captcha
    private static String passwordString(JPasswordField pw_field) {

        String pw = "";
        char[] pw_array = pw_field.getPassword();
        for (int i = 0; i < pw_array.length; i++) {

            pw += pw_array[i];

        }

        return pw;

    }


    //testing if the social security number is correct
    public static boolean isSVNR(String input) {

        int sum = 0;
        if (input.length() != 10) {

            return false;

        } else {

            try {

                sum += Character.getNumericValue(input.charAt(0)) * 3;
                sum += Character.getNumericValue(input.charAt(1)) * 7;
                sum += Character.getNumericValue(input.charAt(2)) * 9;
                sum += Character.getNumericValue(input.charAt(4)) * 5;
                sum += Character.getNumericValue(input.charAt(5)) * 8;
                sum += Character.getNumericValue(input.charAt(6)) * 4;
                sum += Character.getNumericValue(input.charAt(7)) * 2;
                sum += Character.getNumericValue(input.charAt(8)) * 1;
                sum += Character.getNumericValue(input.charAt(9)) * 6;
                sum = sum % 11;
                if (Character.getNumericValue(input.charAt(3)) == sum) {

                    return true;

                } else {

                    return false;

                }

            } catch (NumberFormatException ex) {

                return false;

            }

        }

    }

    //testing if something is a word
    public static boolean isWord(String input) {

        input = input.trim();

        if (Pattern.matches("[a-zäöüA-ZÄÖÜ]+", input)) {

            return true;

        } else {

            return false;

        }

    }

    //testing if something is a string
    public static boolean isString(String input) {

        input = input.trim();
        int richtig = 0;
        String[] words = input.split("\\s");
        for (int i = 0; i < words.length; i++) {

            if (Pattern.matches("[a-zäöüA-ZÄÖÜ]+", words[i])) {

                richtig += 1;

            } else {

                richtig += 0;

            }

        }

        if (richtig == words.length) {

            return true;

        } else {

            return false;

        }

    }

    //testing if something is a number
    public static boolean isNumber(String input) {

        input = input.trim();

        if (Pattern.matches("[0-9]+", input)) {

            return true;

        } else {

            return false;

        }

    }

    //generating an integer from a string
    public static int toInt(String input) {

        String numbers = "0123456789";
        int int_num = 0;
        char[] num_array = numbers.toCharArray();
        char[] in_array = input.toCharArray();
        for (int i = 0; i < in_array.length; i++) {

            for (int j = 0; j < num_array.length; j++) {

                if (in_array[i] == num_array[j] && i != in_array.length - 1) {

                    int_num += j;
                    int_num *= 10;

                } else if (in_array[i] == num_array[j] && i == in_array.length - 1) {

                    int_num += j;

                }

            }

        }

        return int_num;

    }

    //testing if something is an address
    public static boolean isAdress(String plz, String name) {

        if (isNumber(plz) && isString(name)) {

            if(readXML("Addresses.xml", "address", plz, name)) {

                return true;

            } else {

                return false;

            }

        } else {

            return false;

        }

    }

    //creating the xml file "addresses"
    public static void createXML(String tagname, String childname, String filename) {

        File file = new File(filename);
        if (!file.exists()) {

            try {

                DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFac.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                org.w3c.dom.Element rootElem = doc.createElement(tagname);
                doc.appendChild(rootElem);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);

            } catch (TransformerConfigurationException e1) {
                e1.printStackTrace();
            } catch (TransformerException e1) {
                e1.printStackTrace();
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            }
        }
    }

    //appending if something is a number
    public static boolean appendXML(String filename, String tagname, int plz, String stadt) {

        try {

            File file = new File(filename);
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            org.w3c.dom.Element root = doc.getDocumentElement();
            if (!readXML(filename, "address", String.valueOf(plz), stadt)) {

                org.w3c.dom.Element newAddress = doc.createElement(tagname);

                org.w3c.dom.Element postlz = doc.createElement("plz");
                postlz.appendChild(doc.createTextNode(String.valueOf(plz)));
                newAddress.appendChild(postlz);

                org.w3c.dom.Element city = doc.createElement("city");
                city.appendChild(doc.createTextNode(stadt));
                newAddress.appendChild(city);

                root.appendChild(newAddress);

                DOMSource source = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
                return true;

            } else {

                return false;

            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return false;

    }

    //reading the xml file
    public static boolean readXML(String filename, String tagname, String plz, String stadt) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {

            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filename);
            NodeList addressList = doc.getElementsByTagName(tagname);
            for (int i = 0; i < addressList.getLength(); i++) {

                Node p = addressList.item(i);
                if (p.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element person = (org.w3c.dom.Element) p;
                    //String id = person.getAttribute("id");
                    NodeList childList = person.getChildNodes();
                    String pruef_address = "";
                    for (int j = 0; j < childList.getLength(); j++) {

                        Node n = childList.item(j);
                        if (n.getNodeType() == Node.ELEMENT_NODE) {

                            org.w3c.dom.Element name = (org.w3c.dom.Element) n;
                            String content = name.getTextContent();
                            pruef_address += content;

                        }

                    }
                    String input_address = plz + stadt;
                    //System.out.println(input_address + " = " + pruef_address);
                    if (pruef_address.equals(input_address)) {

                        return true;

                    } else {

                        pruef_address = "";

                    }

                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }
}