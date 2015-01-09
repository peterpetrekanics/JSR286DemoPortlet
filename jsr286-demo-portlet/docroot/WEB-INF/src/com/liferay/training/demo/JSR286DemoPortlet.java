package com.liferay.training.demo;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.encoding.MacRomanEncoding;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;



/**
 * Portlet implementation class DemoPortlet
 */
public class JSR286DemoPortlet extends GenericPortlet {

    public void init() {
    	
        editTemplate = getInitParameter("edit-template");
        helpTemplate = getInitParameter("help-template");
        viewTemplate = getInitParameter("view-template");
        
        System.out.println("Inside of init()");
    }

    public void destroy() {
        System.out.println("Inside of destroy()");
    	super.destroy();
    }

    public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException{
    	
    	System.out.println("Inside of render()");
    	
    	super.render(renderRequest, renderResponse);
    	
    }
    
	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
		
		//System.out.println("Inside doDispatch()");
		
		super.doDispatch(renderRequest, renderResponse);
		
	}

    public void doEdit(
            RenderRequest renderRequest, RenderResponse renderResponse)
        throws IOException, PortletException {
        
    	//System.out.println("Inside doEdit()");

    	include(editTemplate, renderRequest, renderResponse);
    }
    
    public void doHelp(
            RenderRequest renderRequest, RenderResponse renderResponse)
        throws IOException, PortletException {
        
    	//System.out.println("Inside doHelp()");
    	
        include(helpTemplate, renderRequest, renderResponse);
    }
    
    public void doView(
            RenderRequest renderRequest, RenderResponse renderResponse)
        throws IOException, PortletException {
        
    	//System.out.println("Inside doView()");
    	
        include(viewTemplate, renderRequest, renderResponse);
    }

    public void processAction(
            ActionRequest actionRequest, ActionResponse actionResponse)
        throws IOException, PortletException {
    	
    	System.out.println("Inside of processAction()");
    	
    	File pdf = new File("C://temp//testdoc9.pdf");
		if (pdf.exists()) {
			ServiceContext serviceContext = new ServiceContext();
			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext
					.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			DLFileEntry fileEntry = null;
			
			//long groupId2 = serviceContext.getCompanyId();
			
			try {
				//System.out.println("company id: " + GroupLocalServiceUtil.getCompanyGroup(PortalUtil.getDefaultCompanyId()).getGroupId());
				
				System.out.println("user id: " + PortalUtil.getUserId(actionRequest));
				System.out.println("company id: " + PortalUtil.getCompanyId(actionRequest));
				//System.out.println("user id: " + ThemeDisplay.getLayout().getGroupId());
				System.out.println("group id: " + GroupLocalServiceUtil.getCompanyGroup(PortalUtil.getCompanyId(actionRequest)).getGroupId());
				
				
				
			} catch (PortalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				/*try {
					//companyid:10154,userid:10196,defgroupid:10180,folderid:10812
					fileEntry = DLFileEntryLocalServiceUtil
							.addFileEntry(
									10196,
									10180,
									10180,
									10812,
									pdf.getName(), MimeTypesUtil
											.getContentType(pdf),
									pdf.getName(), "", "", 0, null, pdf, null,
									pdf.length(), serviceContext);
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			
			if (fileEntry != null) {
				try {
					fileEntry = DLFileEntryLocalServiceUtil.updateFileEntry(
							10196, fileEntry.getFileEntryId(), pdf.getName(),
							fileEntry.getMimeType(), pdf.getName(), "", "",
							false, fileEntry.getFileEntryTypeId(), null, pdf,
							null, pdf.length(), serviceContext);
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

    	
    	
    }
    
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
	throws PortletException, IOException{
	
		System.out.println("Inside serveResource()");
		resourceResponse.setContentType("text/html");
		resourceResponse.getWriter().write("Resource served successfully!");
	}


    protected void include(
            String path, RenderRequest renderRequest,
            RenderResponse renderResponse)
        throws IOException, PortletException {

        PortletRequestDispatcher portletRequestDispatcher =
            getPortletContext().getRequestDispatcher(path);

        if (portletRequestDispatcher == null) {
            _log.error(path + " is not a valid include");
        }
        else {
            portletRequestDispatcher.include(renderRequest, renderResponse);
        }
    }
 
    protected String editTemplate;
    protected String helpTemplate;
    protected String viewTemplate;

    private static Log _log = LogFactoryUtil.getLog(JSR286DemoPortlet.class);

}
