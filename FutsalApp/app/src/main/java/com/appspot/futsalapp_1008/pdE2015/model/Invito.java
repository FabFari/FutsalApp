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
 * Model definition for Invito.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Invito extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime dataInvio;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String emailMittente;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long gruppo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String linkDestinatario;

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getDataInvio() {
    return dataInvio;
  }

  /**
   * @param dataInvio dataInvio or {@code null} for none
   */
  public Invito setDataInvio(com.google.api.client.util.DateTime dataInvio) {
    this.dataInvio = dataInvio;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmailMittente() {
    return emailMittente;
  }

  /**
   * @param emailMittente emailMittente or {@code null} for none
   */
  public Invito setEmailMittente(java.lang.String emailMittente) {
    this.emailMittente = emailMittente;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getGruppo() {
    return gruppo;
  }

  /**
   * @param gruppo gruppo or {@code null} for none
   */
  public Invito setGruppo(java.lang.Long gruppo) {
    this.gruppo = gruppo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Invito setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLinkDestinatario() {
    return linkDestinatario;
  }

  /**
   * @param linkDestinatario linkDestinatario or {@code null} for none
   */
  public Invito setLinkDestinatario(java.lang.String linkDestinatario) {
    this.linkDestinatario = linkDestinatario;
    return this;
  }

  @Override
  public Invito set(String fieldName, Object value) {
    return (Invito) super.set(fieldName, value);
  }

  @Override
  public Invito clone() {
    return (Invito) super.clone();
  }

}
