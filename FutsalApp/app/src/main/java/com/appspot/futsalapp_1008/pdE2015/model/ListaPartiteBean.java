/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-06-30 18:20:40 UTC)
 * on 2015-07-17 at 09:39:53 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for ListaPartiteBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ListaPartiteBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String httpCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Partita> listaPartite;

  static {
    // hack to force ProGuard to consider Partita used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(Partita.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String result;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHttpCode() {
    return httpCode;
  }

  /**
   * @param httpCode httpCode or {@code null} for none
   */
  public ListaPartiteBean setHttpCode(java.lang.String httpCode) {
    this.httpCode = httpCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Partita> getListaPartite() {
    return listaPartite;
  }

  /**
   * @param listaPartite listaPartite or {@code null} for none
   */
  public ListaPartiteBean setListaPartite(java.util.List<Partita> listaPartite) {
    this.listaPartite = listaPartite;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getResult() {
    return result;
  }

  /**
   * @param result result or {@code null} for none
   */
  public ListaPartiteBean setResult(java.lang.String result) {
    this.result = result;
    return this;
  }

  @Override
  public ListaPartiteBean set(String fieldName, Object value) {
    return (ListaPartiteBean) super.set(fieldName, value);
  }

  @Override
  public ListaPartiteBean clone() {
    return (ListaPartiteBean) super.clone();
  }

}
