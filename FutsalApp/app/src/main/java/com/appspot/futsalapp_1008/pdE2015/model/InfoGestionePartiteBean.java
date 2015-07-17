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
 * on 2015-07-16 at 22:05:36 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for InfoGestionePartiteBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class InfoGestionePartiteBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String emailGiocatore;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long idPartita;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer nAmici;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmailGiocatore() {
    return emailGiocatore;
  }

  /**
   * @param emailGiocatore emailGiocatore or {@code null} for none
   */
  public InfoGestionePartiteBean setEmailGiocatore(java.lang.String emailGiocatore) {
    this.emailGiocatore = emailGiocatore;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getIdPartita() {
    return idPartita;
  }

  /**
   * @param idPartita idPartita or {@code null} for none
   */
  public InfoGestionePartiteBean setIdPartita(java.lang.Long idPartita) {
    this.idPartita = idPartita;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNAmici() {
    return nAmici;
  }

  /**
   * @param nAmici nAmici or {@code null} for none
   */
  public InfoGestionePartiteBean setNAmici(java.lang.Integer nAmici) {
    this.nAmici = nAmici;
    return this;
  }

  @Override
  public InfoGestionePartiteBean set(String fieldName, Object value) {
    return (InfoGestionePartiteBean) super.set(fieldName, value);
  }

  @Override
  public InfoGestionePartiteBean clone() {
    return (InfoGestionePartiteBean) super.clone();
  }

}
