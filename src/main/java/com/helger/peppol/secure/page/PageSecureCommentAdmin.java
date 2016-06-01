/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.secure.page;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.Translatable;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.display.IHasDisplayText;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.util.TextHelper;
import com.helger.commons.type.ObjectType;
import com.helger.commons.type.TypedObject;
import com.helger.commons.url.ISimpleURL;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.CommentThreadObjectTypeManager;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.peppol.comment.ui.CommentAction;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentAction;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.nav.BootstrapTabBox;
import com.helger.photon.bootstrap3.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.tabbox.ITabBox;
import com.helger.photon.uicore.html.toolbar.IButtonToolbar;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.AbstractWebPageForm;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.photon.uictrls.datatables.column.EDTColType;

public final class PageSecureCommentAdmin extends AbstractAppWebPage
{
  @Translatable
  protected static enum EText implements IHasDisplayText
  {
    HEADER_OBJECT_ID ("Objekt-ID", "Object ID"),
    HEADER_THREADS ("Themen", "Threads"),
    HEADER_COMMENTS ("Einträge", "Comments"),
    HEADER_ACTIVE_COMMENTS ("Aktive Einträge", "Active comments"),
    HEADER_LAST_CHANGE ("Letzte Änderung", "Last change");

    private final IMultilingualText m_aTP;

    private EText (final String sDE, final String sEN)
    {
      m_aTP = TextHelper.create_DE_EN (sDE, sEN);
    }

    @Nullable
    public String getDisplayText (@Nonnull final Locale aContentLocale)
    {
      return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
    }
  }

  private static final String PARAM_TYPE = "type";

  public PageSecureCommentAdmin (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Comment administration");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CommentThreadManager aCommentThreadMgr = CommentThreadManager.getInstance ();
    boolean bShowList = true;
    final String sSelectedObjectType = aWPEC.getAttributeAsString (PARAM_TYPE);
    final String sSelectedOwningObjectID = aWPEC.getAttributeAsString (CPageParam.PARAM_OBJECT);

    if (aWPEC.hasAction (CPageParam.ACTION_VIEW) &&
        StringHelper.hasText (sSelectedObjectType) &&
        StringHelper.hasText (sSelectedOwningObjectID))
    {
      final TypedObject <String> aTO = TypedObject.create (new ObjectType (sSelectedObjectType),
                                                           sSelectedOwningObjectID);
      final List <ICommentThread> aCommentThreads = aCommentThreadMgr.getCommentThreadsOfObject (aTO);
      if (!aCommentThreads.isEmpty ())
      {
        // Don't allow comment creation
        aNodeList.addChild (CommentUI.getCommentList (aWPEC,
                                                      aTO,
                                                      CommentAction.createGeneric (ECommentAction.NONE),
                                                      null,
                                                      null,
                                                      false));
        // Toolbar
        final IButtonToolbar <?> aToolbar = new BootstrapButtonToolbar (aWPEC);
        aToolbar.addButton (EPhotonCoreText.BACK_TO_OVERVIEW.getDisplayText (aDisplayLocale),
                            aWPEC.getSelfHref (),
                            EDefaultIcon.BACK_TO_LIST);
        aNodeList.addChild (aToolbar);

        bShowList = false;
      }
    }

    if (bShowList)
    {
      // Refresh button
      final IButtonToolbar <?> aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addButton (EPhotonCoreText.BUTTON_REFRESH.getDisplayText (aDisplayLocale),
                          aWPEC.getSelfHref (),
                          EDefaultIcon.REFRESH);
      aNodeList.addChild (aToolbar);

      final ITabBox <?> aTabBox = new BootstrapTabBox ();
      for (final ObjectType aOT : CollectionHelper.getSorted (aCommentThreadMgr.getAllRegisteredObjectTypes ()))
      {
        final HCNodeList aTab = new HCNodeList ();

        final HCTable aTable = new HCTable (new DTCol (EText.HEADER_OBJECT_ID.getDisplayText (aDisplayLocale)),
                                            new DTCol (EText.HEADER_THREADS.getDisplayText (aDisplayLocale)).addClass (CSS_CLASS_RIGHT)
                                                                                                            .setDisplayType (EDTColType.INT,
                                                                                                                             aDisplayLocale),
                                            new DTCol (EText.HEADER_COMMENTS.getDisplayText (aDisplayLocale)).addClass (CSS_CLASS_RIGHT)
                                                                                                             .setDisplayType (EDTColType.INT,
                                                                                                                              aDisplayLocale),
                                            new DTCol (EText.HEADER_ACTIVE_COMMENTS.getDisplayText (aDisplayLocale)).addClass (CSS_CLASS_RIGHT)
                                                                                                                    .setDisplayType (EDTColType.INT,
                                                                                                                                     aDisplayLocale),
                                            new DTCol (EText.HEADER_LAST_CHANGE.getDisplayText (aDisplayLocale)).addClass (CSS_CLASS_RIGHT)
                                                                                                                .setDisplayType (EDTColType.DATETIME,
                                                                                                                                 aDisplayLocale)
                                                                                                                .setInitialSorting (ESortOrder.DESCENDING)).setID (getID () +
                                                                                                                                                                   aOT.getName ());
        final CommentThreadObjectTypeManager aCTOTMgr = aCommentThreadMgr.getManagerOfObjectType (aOT);
        for (final Map.Entry <String, ICommonsList <ICommentThread>> aEntry : aCTOTMgr.getAllCommentThreads ()
                                                                                      .entrySet ())
        {
          final String sOwningObjectID = aEntry.getKey ();
          final ISimpleURL aViewURL = AbstractWebPageForm.createViewURL (aWPEC, sOwningObjectID).add (PARAM_TYPE,
                                                                                                      aOT.getName ());

          final HCRow aRow = aTable.addBodyRow ();
          aRow.addCell (new HCA (aViewURL).addChild (sOwningObjectID));
          aRow.addCell (Integer.toString (aEntry.getValue ().size ()));

          int nActiveComments = 0;
          final ICommonsList <IComment> aAllComments = new CommonsArrayList<> ();
          for (final ICommentThread aCommentThread : aEntry.getValue ())
          {
            aAllComments.addAll (aCommentThread.getAllComments ());
            nActiveComments += aCommentThread.getTotalActiveCommentCount ();
          }
          Collections.sort (aAllComments, Comparator.comparing (IComment::getLastChangeDateTime).reversed ());

          aRow.addCell (Integer.toString (aAllComments.size ()));
          aRow.addCell (Integer.toString (nActiveComments));

          if (aAllComments.isEmpty ())
            aRow.addCell ();
          else
            aRow.addCell (PDTToString.getAsString (aAllComments.get (0).getLastChangeDateTime (), aDisplayLocale));
        }
        aTab.addChild (aTable);

        final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);
        aTab.addChild (aDataTables);

        aTabBox.addTab (aOT.getName (), aTab, aOT.getName ().equals (sSelectedObjectType));
      }

      aNodeList.addChild (aTabBox);
    }
  }
}
