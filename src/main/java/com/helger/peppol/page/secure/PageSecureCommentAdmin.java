/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.peppol.page.secure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.Translatable;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.name.IHasDisplayText;
import com.helger.commons.name.IHasDisplayTextWithArgs;
import com.helger.commons.text.impl.TextProvider;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.type.ObjectType;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.html.HCTable;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.CommentThreadObjectTypeManager;
import com.helger.peppol.comment.domain.ComparatorCommentLastChange;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.peppol.page.AbstractAppWebPageExt;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.custom.tabbox.ITabBox;
import com.helger.webctrls.custom.toolbar.IButtonToolbar;
import com.helger.webctrls.datatables.DataTables;
import com.helger.webctrls.datatables.comparator.ComparatorDTDateTime;
import com.helger.webctrls.datatables.comparator.ComparatorDTInteger;

public final class PageSecureCommentAdmin extends AbstractAppWebPageExt
{
  @Translatable
  protected static enum EText implements IHasDisplayText, IHasDisplayTextWithArgs
  {
    BUTTON_REFRESH ("Aktualisieren", "Refresh");

    private final TextProvider m_aTP;

    private EText (final String sDE, final String sEN)
    {
      m_aTP = TextProvider.create_DE_EN (sDE, sEN);
    }

    @Nullable
    public String getDisplayText (@Nonnull final Locale aContentLocale)
    {
      return DefaultTextResolver.getText (this, m_aTP, aContentLocale);
    }

    @Nullable
    public String getDisplayTextWithArgs (@Nonnull final Locale aContentLocale, @Nullable final Object... aArgs)
    {
      return DefaultTextResolver.getTextWithArgs (this, m_aTP, aContentLocale, aArgs);
    }
  }

  public PageSecureCommentAdmin (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Comment administration");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    // Refresh button
    final IButtonToolbar <?> aToolbar = getStyler ().createToolbar (aWPEC);
    aToolbar.addButton (EText.BUTTON_REFRESH.getDisplayText (aDisplayLocale),
                        aWPEC.getSelfHref (),
                        EDefaultIcon.REFRESH);
    aNodeList.addChild (aToolbar);

    final ITabBox <?> aTabBox = getStyler ().createTabBox (aWPEC);
    final CommentThreadManager aCommentThreadMgr = CommentThreadManager.getInstance ();
    for (final ObjectType aOT : ContainerHelper.getSorted (aCommentThreadMgr.getAllRegisteredObjectTypes ()))
    {
      final HCNodeList aTab = new HCNodeList ();

      final HCTable aTable = new HCTable (HCCol.star (), HCCol.star (), HCCol.star (), HCCol.star ()).setID (getID () +
                                                                                                             aOT.getObjectTypeName ());
      aTable.addHeaderRow ().addCells ("Object ID", "Comment threads", "Comments", "Last change");
      final CommentThreadObjectTypeManager aCTOTMgr = aCommentThreadMgr.getManagerOfObjectType (aOT);
      for (final Map.Entry <String, List <ICommentThread>> aEntry : aCTOTMgr.getAllCommentThreads ().entrySet ())
      {
        final String sOwningObjectID = aEntry.getKey ();

        final HCRow aRow = aTable.addBodyRow ();
        aRow.addCell (sOwningObjectID);
        aRow.addCell (Integer.toString (aEntry.getValue ().size ()));

        final List <IComment> aAllComments = new ArrayList <IComment> ();
        for (final ICommentThread aCommentThread : aEntry.getValue ())
          aAllComments.addAll (aCommentThread.getAllComments ());
        Collections.sort (aAllComments, new ComparatorCommentLastChange (ESortOrder.DESCENDING));

        aRow.addCell (Integer.toString (aAllComments.size ()));

        if (aAllComments.isEmpty ())
          aRow.addCell ();
        else
          aRow.addCell (PDTToString.getAsString (aAllComments.get (0).getLastChangeDateTime (), aDisplayLocale));
      }
      aTab.addChild (aTable);

      final DataTables aDataTables = getStyler ().createDefaultDataTables (aWPEC, aTable);
      aDataTables.getOrCreateColumnOfTarget (1).setComparator (new ComparatorDTInteger (aDisplayLocale));
      aDataTables.getOrCreateColumnOfTarget (2).setComparator (new ComparatorDTInteger (aDisplayLocale));
      aDataTables.getOrCreateColumnOfTarget (3).setComparator (new ComparatorDTDateTime (aDisplayLocale));
      aDataTables.setInitialSorting (3, ESortOrder.DESCENDING);
      aTab.addChild (aDataTables);

      aTabBox.addTab (aOT.getObjectTypeName (), aTab);
    }

    aNodeList.addChild (aTabBox);
  }
}
