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
 * (build: 2015-07-16 18:28:29 UTC)
 * on 2015-07-23 at 13:35:55 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for Partita.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Partita extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long campo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime dataOra;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime dataOraPartita;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> linkDisponibile;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> linkGioca;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long linkOrganizza;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> linkVotoPerPartita;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer npartecipanti;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String propone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float quota;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String statoCorrente;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCampo() {
    return campo;
  }

  /**
   * @param campo campo or {@code null} for none
   */
  public Partita setCampo(java.lang.Long campo) {
    this.campo = campo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getDataOra() {
    return dataOra;
  }

  /**
   * @param dataOra dataOra or {@code null} for none
   */
  public Partita setDataOra(com.google.api.client.util.DateTime dataOra) {
    this.dataOra = dataOra;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getDataOraPartita() {
    return dataOraPartita;
  }

  /**
   * @param dataOraPartita dataOraPartita or {@code null} for none
   */
  public Partita setDataOraPartita(com.google.api.client.util.DateTime dataOraPartita) {
    this.dataOraPartita = dataOraPartita;
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
  public Partita setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getLinkDisponibile() {
    return linkDisponibile;
  }

  /**
   * @param linkDisponibile linkDisponibile or {@code null} for none
   */
  public Partita setLinkDisponibile(java.util.List<java.lang.Long> linkDisponibile) {
    this.linkDisponibile = linkDisponibile;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getLinkGioca() {
    return linkGioca;
  }

  /**
   * @param linkGioca linkGioca or {@code null} for none
   */
  public Partita setLinkGioca(java.util.List<java.lang.String> linkGioca) {
    this.linkGioca = linkGioca;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getLinkOrganizza() {
    return linkOrganizza;
  }

  /**
   * @param linkOrganizza linkOrganizza or {@code null} for none
   */
  public Partita setLinkOrganizza(java.lang.Long linkOrganizza) {
    this.linkOrganizza = linkOrganizza;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getLinkVotoPerPartita() {
    return linkVotoPerPartita;
  }

  /**
   * @param linkVotoPerPartita linkVotoPerPartita or {@code null} for none
   */
  public Partita setLinkVotoPerPartita(java.util.List<java.lang.Long> linkVotoPerPartita) {
    this.linkVotoPerPartita = linkVotoPerPartita;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNpartecipanti() {
    return npartecipanti;
  }

  /**
   * @param npartecipanti npartecipanti or {@code null} for none
   */
  public Partita setNpartecipanti(java.lang.Integer npartecipanti) {
    this.npartecipanti = npartecipanti;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPropone() {
    return propone;
  }

  /**
   * @param propone propone or {@code null} for none
   */
  public Partita setPropone(java.lang.String propone) {
    this.propone = propone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getQuota() {
    return quota;
  }

  /**
   * @param quota quota or {@code null} for none
   */
  public Partita setQuota(java.lang.Float quota) {
    this.quota = quota;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getStatoCorrente() {
    return statoCorrente;
  }

  /**
   * @param statoCorrente statoCorrente or {@code null} for none
   */
  public Partita setStatoCorrente(java.lang.String statoCorrente) {
    this.statoCorrente = statoCorrente;
    return this;
  }

  @Override
  public Partita set(String fieldName, Object value) {
    return (Partita) super.set(fieldName, value);
  }

  @Override
  public Partita clone() {
    return (Partita) super.clone();
  }

}
