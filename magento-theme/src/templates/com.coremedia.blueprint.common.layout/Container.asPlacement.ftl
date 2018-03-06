<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<div class="cm-container container">
  <#list self.items![] as item>
    <@cm.include self=item view="asTeaser"/>
  </#list>
</div>