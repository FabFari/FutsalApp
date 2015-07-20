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
 * on 2015-07-20 at 08:20:34 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for VotoUomoPartita.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class VotoUomoPartita extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String commento;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long linkVotoPerPartita;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String votanteUP;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String votatoUP;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCommento() {
    return commento;
  }

  /**
   * @param commento commento or {@code null} for none
   */
  public VotoUomoPartita setCommento(java.lang.String commento) {
    this.commento = commento;
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
  public VotoUomoPartita setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getLinkVotoPerPartita() {
    return linkVotoPerPartita;
  }

  /**
   * @param linkVotoPerPartita linkVotoPerPartita or {@code null} for none
   */
  public VotoUomoPartita setLinkVotoPerPartita(java.lang.Long linkVotoPerPartita) {
    this.linkVotoPerPartita = linkVotoPerPartita;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getVotanteUP() {
    return votanteUP;
  }

  /**
   * @param votanteUP votanteUP or {@code null} for none
   */
  public VotoUomoPartita setVotanteUP(java.lang.String votanteUP) {
    this.votanteUP = votanteUP;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getVotatoUP() {
    return votatoUP;
  }

  /**
   * @param votatoUP votatoUP or {@code null} for none
   */
  public VotoUomoPartita setVotatoUP(java.lang.String votatoUP) {
    this.votatoUP = votatoUP;
    return this;
  }

  @Override
  public VotoUomoPartita set(String fieldName, Object value) {
    return (VotoUomoPartita) super.set(fieldName, value);
  }

  @Override
  public VotoUomoPartita clone() {
    return (VotoUomoPartita) super.clone();
  }

}
