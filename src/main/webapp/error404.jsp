<%--

    Copyright (C) 2014 Philip Helger (www.helger.com)
    philip[at]helger[dot]com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%><%
// In Jetty, the request attributes are already URL encoded!
final String sTarget = request.getContextPath () + "/public/?httpError=true"
                       +"&httpStatusCode="
                       +String.valueOf(request.getAttribute ("jakarta.servlet.error.status_code"))
                       +"&httpStatusMessage="
                       +String.valueOf(request.getAttribute ("jakarta.servlet.error.message"))
                       +"&httpRequestUri="
                       +String.valueOf(request.getAttribute ("jakarta.servlet.error.request_uri"))
                       +"&httpReferrer="
                       +request.getHeader ("Referer");
response.sendRedirect (sTarget);
%>