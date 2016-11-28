package com.helger.peppol.ui;

import javax.annotation.Nonnull;

import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCConversionSettingsToNode;
import com.helger.html.hc.html.textlevel.AbstractHCA;
import com.helger.html.resource.js.ConstantJSPathProvider;
import com.helger.photon.core.app.html.PhotonJS;

/**
 * <pre>
 * <a href="https://twitter.com/share" class=
"twitter-share-button" data-show-count=
"false">Tweet</a><script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
 * </pre>
 *
 * @author Philip Helger
 */
public class HCTweet extends AbstractHCA <HCTweet>
{
  public static final ICSSClassProvider CSS_TWITTER_SHARE_BUTTON = DefaultCSSClassProvider.create ("twitter-share-button");
  public static final ICSSClassProvider CSS_TWITTER_HASHTAG_BUTTON = DefaultCSSClassProvider.create ("twitter-hashtag-button");

  public static final ISimpleURL URL_SHARE = new SimpleURL ("https://twitter.com/share");
  public static final ISimpleURL URL_TWEET = new SimpleURL ("https://twitter.com/intent/tweet");

  public HCTweet ()
  {}

  @Override
  public boolean canConvertToMicroNode (@Nonnull final IHCConversionSettingsToNode aConversionSettings)
  {
    // Render "tweet" nodes only in production mode
    return GlobalDebug.isProductionMode ();
  }

  @Override
  protected void onRegisterExternalResources (@Nonnull final IHCConversionSettingsToNode aConversionSettings,
                                              final boolean bForceRegistration)
  {
    super.onRegisterExternalResources (aConversionSettings, bForceRegistration);
    // TODO ph-html > 6.0.5 use .createExternal (String)
    PhotonJS.registerJSIncludeForThisRequest (new ConstantJSPathProvider ("https://platform.twitter.com/widgets.js",
                                                                          "https://platform.twitter.com/widgets.js",
                                                                          null,
                                                                          false));
  }

  @Nonnull
  public static HCTweet createShareButton ()
  {
    return new HCTweet ().addClass (CSS_TWITTER_SHARE_BUTTON)
                         .setHref (URL_SHARE)
                         .addChild ("Tweet")
                         .setDataAttr ("show-count", "false");
  }
}
