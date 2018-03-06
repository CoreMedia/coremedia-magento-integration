<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<@cm.include self=self view="detail" params={
"renderTags": false,
"carouselParams": {
"carouselItemParams": {
"renderTitle": false,
"renderText": false
}
}
}/>
