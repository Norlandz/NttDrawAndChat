module drawAndChatApp {
  exports com.redfrog.note;
  exports com.redfrog.note.communication;
  exports com.redfrog.note.controller;
  exports com.redfrog.note.convert;
  exports com.redfrog.note.database;
  exports com.redfrog.note.event;
  exports com.redfrog.note.event.mousekeyboardinput;
  exports com.redfrog.note.event.nodestatus;
  exports com.redfrog.note.event.session;
  exports com.redfrog.note.event.traversal;
  exports com.redfrog.note.exception;
  exports com.redfrog.note.fundamental;
  exports com.redfrog.note.log;
  exports com.redfrog.note.nodeRole;
  exports com.redfrog.note.nodeShape;
  exports com.redfrog.note.serialization;
  exports com.redfrog.note.session;
  exports com.redfrog.note.traversal;
  exports com.redfrog.note.user;
  exports com.redfrog.note.util;

  opens com.redfrog.note;
  opens com.redfrog.note.communication;
  opens com.redfrog.note.controller;
  opens com.redfrog.note.convert;
  opens com.redfrog.note.database;
  opens com.redfrog.note.event;
  opens com.redfrog.note.event.mousekeyboardinput;
  opens com.redfrog.note.event.nodestatus;
  opens com.redfrog.note.event.session;
  opens com.redfrog.note.event.traversal;
  opens com.redfrog.note.exception;
  opens com.redfrog.note.fundamental;
  opens com.redfrog.note.log;
  opens com.redfrog.note.nodeRole;
  opens com.redfrog.note.nodeShape;
  opens com.redfrog.note.serialization;
  opens com.redfrog.note.session;
  opens com.redfrog.note.traversal;
  opens com.redfrog.note.user;
  opens com.redfrog.note.util;

  requires transitive kafka.clients;

  requires transitive java.desktop;
  requires transitive javafx.base;
  requires transitive javafx.controls;
  requires transitive javafx.graphics;
  requires transitive javafx.swing;

  requires transitive spring.core;
  requires transitive spring.beans;
  requires transitive spring.context;
  requires transitive spring.web;
  requires transitive spring.boot;
  requires transitive spring.boot.autoconfigure;

  requires transitive spring.data.commons;
  requires transitive spring.jdbc;
  requires transitive spring.orm;
  requires transitive spring.boot.starter.data.jpa;
  requires transitive spring.tx;
  requires transitive java.sql;
  requires transitive java.transaction;
  requires transitive java.persistence;
  requires transitive org.hibernate.commons.annotations;

  requires transitive spring.aop;
  requires transitive spring.aspects;

  requires transitive lombok;
  requires transitive org.slf4j;
  requires transitive org.hibernate.orm.core;

  requires transitive org.apache.commons.lang3;
  requires transitive com.google.common;

  requires transitive com.fasterxml.jackson.core;
  requires transitive com.fasterxml.jackson.databind;
  requires transitive com.fasterxml.jackson.annotation;
  requires datasource.proxy;
  requires org.apache.commons.io;

}