/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.url.SimpleURL;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCConversionSettingsToNode;
import com.helger.html.hc.IHCHasChildrenMutable;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.grouping.AbstractHCDiv;
import com.helger.html.hc.html.script.HCScriptInline;
import com.helger.html.jscode.JSFunction;
import com.helger.html.jscode.html.JSHtml;
import com.helger.html.resource.js.ConstantJSPathProvider;
import com.helger.photon.app.html.PhotonJS;

/**
 * Handle Google reCAPTCHA according to
 * https://developers.google.com/recaptcha/docs/display
 *
 * <pre>
 * &lt;div class="g-recaptcha" data-sitekey="your_site_key" data-callback="js-function" data-action="submit"&gt;&lt;/div&gt;
 * </pre>
 *
 * @author Philip Helger
 */
public class HCReCaptchaV3 extends AbstractHCDiv <HCReCaptchaV3>
{
  public static final ICSSClassProvider CSS_G_RECAPTCHA = DefaultCSSClassProvider.create ("g-recaptcha");
  public static final String RESPONSE_PARAMETER_NAME = "g-recaptcha-response";

  private final String m_sFunctionName;
  private final String m_sDisplayLanguage;
  private final String m_sFormID;

  public HCReCaptchaV3 (@Nonnull @Nonempty final String sSiteKey,
                        @Nullable final String sDisplayLanguage,
                        @Nonnull final String sFormID)
  {
    ValueEnforcer.notEmpty (sSiteKey, "SiteKey");
    ValueEnforcer.notEmpty (sFormID, "FormID");

    m_sFunctionName = "onRecaptchaSubmit" + GlobalIDFactory.getNewIntID ();

    addClass (CSS_G_RECAPTCHA);
    customAttrs ().setDataAttr ("sitekey", sSiteKey);
    customAttrs ().setDataAttr ("callback", m_sFunctionName);
    customAttrs ().setDataAttr ("action", "submit");
    m_sDisplayLanguage = sDisplayLanguage;
    m_sFormID = sFormID;
  }

  /**
   * @return The display language as passed in the constructor. May be
   *         <code>null</code>.
   */
  @Nullable
  public final String getDisplayLanguage ()
  {
    return m_sDisplayLanguage;
  }

  @Override
  protected void onFinalizeNodeState (final IHCConversionSettingsToNode aConversionSettings,
                                      final IHCHasChildrenMutable <?, ? super IHCNode> aTargetNode)
  {
    final JSFunction jsFunc = new JSFunction (m_sFunctionName);
    jsFunc.body ().add (JSHtml.documentGetElementById (m_sFormID).invoke ("submit"));
    aTargetNode.addChild (new HCScriptInline (jsFunc));
  }

  @Override
  protected void onRegisterExternalResources (@Nonnull final IHCConversionSettingsToNode aConversionSettings,
                                              final boolean bForceRegistration)
  {
    super.onRegisterExternalResources (aConversionSettings, bForceRegistration);

    final SimpleURL aURL = new SimpleURL ("https://www.google.com/recaptcha/api.js");
    if (m_sDisplayLanguage != null)
      aURL.add ("hl", m_sDisplayLanguage);
    PhotonJS.registerJSIncludeForThisRequest (ConstantJSPathProvider.createExternal (aURL.getAsStringWithEncodedParameters ()));
  }
}
